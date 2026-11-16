package br.com.hotel.reservas.dao;

import br.com.hotel.reservas.model.Reserva;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Acesso à tabela "reservas" usando JDBC puro (Connection + PreparedStatement). */
@Repository
public class ReservaDAO {

    private static final String SELECT =
            "SELECT r.id, r.cliente_id, c.nome, c.sexo, c.email, "
                    + "r.data_chegada, r.numero_noites, r.numero_hospedes, r.mensagem "
                    + "FROM reservas r JOIN clientes c ON c.id = r.cliente_id ";

    public long inserir(Connection conn, long clienteId, LocalDate dataChegada,
                        int numeroNoites, int numeroHospedes, String mensagem) throws SQLException {
        String sql = "INSERT INTO reservas (cliente_id, data_chegada, numero_noites, numero_hospedes, mensagem) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, clienteId);
            ps.setDate(2, Date.valueOf(dataChegada));
            ps.setInt(3, numeroNoites);
            ps.setInt(4, numeroHospedes);
            ps.setString(5, mensagem);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        }
    }

    public Optional<Reserva> buscarPorId(Connection conn, long id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SELECT + "WHERE r.id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public List<Reserva> listarPorEmail(Connection conn, String email) throws SQLException {
        List<Reserva> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT + "WHERE c.email = ? ORDER BY r.id")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public boolean atualizar(Connection conn, long id, LocalDate dataChegada,
                             int numeroNoites, int numeroHospedes, String mensagem) throws SQLException {
        String sql = "UPDATE reservas SET data_chegada = ?, numero_noites = ?, numero_hospedes = ?, mensagem = ? "
                + "WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(dataChegada));
            ps.setInt(2, numeroNoites);
            ps.setInt(3, numeroHospedes);
            ps.setString(4, mensagem);
            ps.setLong(5, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean excluir(Connection conn, long id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM reservas WHERE id = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Reserva mapear(ResultSet rs) throws SQLException {
        return new Reserva(
                rs.getLong("id"),
                rs.getLong("cliente_id"),
                rs.getString("nome"),
                rs.getString("sexo"),
                rs.getString("email"),
                rs.getDate("data_chegada").toLocalDate(),
                rs.getInt("numero_noites"),
                rs.getInt("numero_hospedes"),
                rs.getString("mensagem"));
    }
}

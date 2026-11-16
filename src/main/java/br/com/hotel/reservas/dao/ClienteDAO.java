package br.com.hotel.reservas.dao;

import br.com.hotel.reservas.model.Cliente;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Acesso à tabela "clientes" usando JDBC puro (Connection + PreparedStatement). */
@Repository
public class ClienteDAO {

    private static final String SELECT = "SELECT id, nome, sexo, email FROM clientes";

    public Cliente inserir(Connection conn, String nome, String sexo, String email) throws SQLException {
        String sql = "INSERT INTO clientes (nome, sexo, email) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, nome);
            ps.setString(2, sexo);
            ps.setString(3, email);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return new Cliente(keys.getLong(1), nome, sexo, email);
            }
        }
    }

    public List<Cliente> listar(Connection conn) throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(SELECT + " ORDER BY id");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Optional<Cliente> buscarPorId(Connection conn, long id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SELECT + " WHERE id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public Optional<Cliente> buscarPorEmail(Connection conn, String email) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(SELECT + " WHERE email = ?")) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        }
    }

    public boolean atualizar(Connection conn, long id, String nome, String sexo, String email) throws SQLException {
        String sql = "UPDATE clientes SET nome = ?, sexo = ?, email = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.setString(2, sexo);
            ps.setString(3, email);
            ps.setLong(4, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean excluir(Connection conn, long id) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM clientes WHERE id = ?")) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    private Cliente mapear(ResultSet rs) throws SQLException {
        return new Cliente(rs.getLong("id"), rs.getString("nome"), rs.getString("sexo"), rs.getString("email"));
    }
}

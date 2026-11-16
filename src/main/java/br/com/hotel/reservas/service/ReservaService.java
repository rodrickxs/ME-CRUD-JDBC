package br.com.hotel.reservas.service;

import br.com.hotel.reservas.dao.ClienteDAO;
import br.com.hotel.reservas.dao.ReservaDAO;
import br.com.hotel.reservas.dto.ReservaRequest;
import br.com.hotel.reservas.dto.ReservaUpdateRequest;
import br.com.hotel.reservas.exception.ApiException;
import br.com.hotel.reservas.model.Cliente;
import br.com.hotel.reservas.model.Reserva;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class ReservaService {

    private static final Pattern EMAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final DataSource dataSource;
    private final ClienteDAO clienteDAO;
    private final ReservaDAO reservaDAO;

    public ReservaService(DataSource dataSource, ClienteDAO clienteDAO, ReservaDAO reservaDAO) {
        this.dataSource = dataSource;
        this.clienteDAO = clienteDAO;
        this.reservaDAO = reservaDAO;
    }

    /**
     * Salva cliente (se o e-mail ainda não existir) e reserva na mesma transação.
     * Se o e-mail já existe, o cliente é reaproveitado e nome/sexo são atualizados.
     */
    public Reserva criar(ReservaRequest req) {
        String nome = req.nome().strip();
        String sexo = req.sexo().strip();
        String email = ClienteService.normalizarEmail(req.email());

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                Optional<Cliente> existente = clienteDAO.buscarPorEmail(conn, email);
                long clienteId;
                if (existente.isPresent()) {
                    clienteId = existente.get().id();
                    clienteDAO.atualizar(conn, clienteId, nome, sexo, email);
                } else {
                    clienteId = clienteDAO.inserir(conn, nome, sexo, email).id();
                }

                long reservaId = reservaDAO.inserir(conn, clienteId, req.dataChegada(),
                        req.numeroNoites(), req.numeroHospedes(), limparMensagem(req.mensagem()));
                Reserva criada = reservaDAO.buscarPorId(conn, reservaId).orElseThrow();
                conn.commit();
                return criada;
            } catch (SQLException | RuntimeException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw ClienteService.erroBanco(e);
        }
    }

    public List<Reserva> listarPorEmail(String emailInformado) {
        if (emailInformado == null || !EMAIL.matcher(emailInformado.strip()).matches()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Informe um e-mail válido no parâmetro 'email'.");
        }
        try (Connection conn = dataSource.getConnection()) {
            return reservaDAO.listarPorEmail(conn, ClienteService.normalizarEmail(emailInformado));
        } catch (SQLException e) {
            throw ClienteService.erroBanco(e);
        }
    }

    public Reserva buscarPorId(long id) {
        try (Connection conn = dataSource.getConnection()) {
            return reservaDAO.buscarPorId(conn, id).orElseThrow(() -> naoEncontrada(id));
        } catch (SQLException e) {
            throw ClienteService.erroBanco(e);
        }
    }

    public Reserva atualizar(long id, ReservaUpdateRequest req) {
        try (Connection conn = dataSource.getConnection()) {
            boolean atualizou = reservaDAO.atualizar(conn, id, req.dataChegada(),
                    req.numeroNoites(), req.numeroHospedes(), limparMensagem(req.mensagem()));
            if (!atualizou && reservaDAO.buscarPorId(conn, id).isEmpty()) {
                throw naoEncontrada(id);
            }
            return reservaDAO.buscarPorId(conn, id).orElseThrow(() -> naoEncontrada(id));
        } catch (SQLException e) {
            throw ClienteService.erroBanco(e);
        }
    }

    public void excluir(long id) {
        try (Connection conn = dataSource.getConnection()) {
            if (!reservaDAO.excluir(conn, id)) {
                throw naoEncontrada(id);
            }
        } catch (SQLException e) {
            throw ClienteService.erroBanco(e);
        }
    }

    private String limparMensagem(String mensagem) {
        return mensagem == null || mensagem.isBlank() ? null : mensagem.strip();
    }

    private ApiException naoEncontrada(long id) {
        return new ApiException(HttpStatus.NOT_FOUND, "Reserva de ID " + id + " não encontrada.");
    }
}

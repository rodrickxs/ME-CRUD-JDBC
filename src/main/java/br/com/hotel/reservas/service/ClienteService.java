package br.com.hotel.reservas.service;

import br.com.hotel.reservas.dao.ClienteDAO;
import br.com.hotel.reservas.dto.ClienteRequest;
import br.com.hotel.reservas.exception.ApiException;
import br.com.hotel.reservas.model.Cliente;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class ClienteService {

    private final DataSource dataSource;
    private final ClienteDAO clienteDAO;

    public ClienteService(DataSource dataSource, ClienteDAO clienteDAO) {
        this.dataSource = dataSource;
        this.clienteDAO = clienteDAO;
    }

    public List<Cliente> listar() {
        try (Connection conn = dataSource.getConnection()) {
            return clienteDAO.listar(conn);
        } catch (SQLException e) {
            throw erroBanco(e);
        }
    }

    public Cliente buscarPorId(long id) {
        try (Connection conn = dataSource.getConnection()) {
            return clienteDAO.buscarPorId(conn, id).orElseThrow(() -> naoEncontrado(id));
        } catch (SQLException e) {
            throw erroBanco(e);
        }
    }

    public Cliente atualizar(long id, ClienteRequest req) {
        String nome = req.nome().strip();
        String sexo = req.sexo().strip();
        String email = normalizarEmail(req.email());

        try (Connection conn = dataSource.getConnection()) {
            if (clienteDAO.buscarPorId(conn, id).isEmpty()) {
                throw naoEncontrado(id);
            }
            Optional<Cliente> outro = clienteDAO.buscarPorEmail(conn, email);
            if (outro.isPresent() && outro.get().id() != id) {
                throw new ApiException(HttpStatus.CONFLICT, "O e-mail " + email + " já pertence a outro cliente.");
            }
            clienteDAO.atualizar(conn, id, nome, sexo, email);
            return new Cliente(id, nome, sexo, email);
        } catch (SQLException e) {
            throw erroBanco(e);
        }
    }

    /** Exclui o cliente; as reservas dele são removidas junto (ON DELETE CASCADE). */
    public void excluir(long id) {
        try (Connection conn = dataSource.getConnection()) {
            if (!clienteDAO.excluir(conn, id)) {
                throw naoEncontrado(id);
            }
        } catch (SQLException e) {
            throw erroBanco(e);
        }
    }

    static String normalizarEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }

    private ApiException naoEncontrado(long id) {
        return new ApiException(HttpStatus.NOT_FOUND, "Cliente de ID " + id + " não encontrado.");
    }

    static ApiException erroBanco(SQLException e) {
        return new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao acessar o banco de dados.", e);
    }
}

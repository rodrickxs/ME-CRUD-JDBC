package br.com.hotel.reservas.controller;

import br.com.hotel.reservas.dto.ClienteRequest;
import br.com.hotel.reservas.model.Cliente;
import br.com.hotel.reservas.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    /** GET /api/clientes */
    @GetMapping
    public List<Cliente> listar() {
        return service.listar();
    }

    /** GET /api/clientes/{id} */
    @GetMapping("/{id}")
    public Cliente buscar(@PathVariable("id") long id) {
        return service.buscarPorId(id);
    }

    /** PUT /api/clientes/{id} - atualiza nome, sexo e e-mail. */
    @PutMapping("/{id}")
    public Cliente atualizar(@PathVariable("id") long id, @Valid @RequestBody ClienteRequest request) {
        return service.atualizar(id, request);
    }

    /** DELETE /api/clientes/{id} - exclui o cliente e as reservas dele. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable("id") long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

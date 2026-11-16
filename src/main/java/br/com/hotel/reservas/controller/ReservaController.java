package br.com.hotel.reservas.controller;

import br.com.hotel.reservas.dto.ReservaRequest;
import br.com.hotel.reservas.dto.ReservaUpdateRequest;
import br.com.hotel.reservas.model.Reserva;
import br.com.hotel.reservas.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {

    private final ReservaService service;

    public ReservaController(ReservaService service) {
        this.service = service;
    }

    /** POST /api/reservas - cria cliente (se necessário) e reserva. */
    @PostMapping
    public ResponseEntity<Reserva> criar(@Valid @RequestBody ReservaRequest request) {
        Reserva reserva = service.criar(request);
        return ResponseEntity.created(URI.create("/api/reservas/" + reserva.id())).body(reserva);
    }

    /** GET /api/reservas?email=... - lista as reservas do e-mail informado. */
    @GetMapping
    public List<Reserva> listarPorEmail(@RequestParam("email") String email) {
        return service.listarPorEmail(email);
    }

    /** GET /api/reservas/{id} */
    @GetMapping("/{id}")
    public Reserva buscar(@PathVariable("id") long id) {
        return service.buscarPorId(id);
    }

    /** PUT /api/reservas/{id} - atualiza dados da estadia. */
    @PutMapping("/{id}")
    public Reserva atualizar(@PathVariable("id") long id, @Valid @RequestBody ReservaUpdateRequest request) {
        return service.atualizar(id, request);
    }

    /** DELETE /api/reservas/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable("id") long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

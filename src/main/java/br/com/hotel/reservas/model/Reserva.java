package br.com.hotel.reservas.model;

import java.time.LocalDate;

/** Reserva já com os dados do cliente (resultado do JOIN). */
public record Reserva(
        Long id,
        Long clienteId,
        String nome,
        String sexo,
        String email,
        LocalDate dataChegada,
        int numeroNoites,
        int numeroHospedes,
        String mensagem
) {
}

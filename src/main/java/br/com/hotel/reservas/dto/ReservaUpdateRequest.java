package br.com.hotel.reservas.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** Corpo do PUT /api/reservas/{id} (só os dados da estadia). */
public record ReservaUpdateRequest(
        @NotNull(message = "dataChegada é obrigatória (formato AAAA-MM-DD)")
        LocalDate dataChegada,

        @NotNull(message = "numeroNoites é obrigatório")
        @Positive(message = "numeroNoites deve ser maior que zero")
        Integer numeroNoites,

        @NotNull(message = "numeroHospedes é obrigatório")
        @Positive(message = "numeroHospedes deve ser maior que zero")
        Integer numeroHospedes,

        @Size(max = 1000, message = "mensagem deve ter no máximo 1000 caracteres")
        String mensagem
) {
}

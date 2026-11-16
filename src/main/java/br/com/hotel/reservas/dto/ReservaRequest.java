package br.com.hotel.reservas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** Corpo do POST /api/reservas (dados do cliente + dados da reserva). */
public record ReservaRequest(
        @NotBlank(message = "nome é obrigatório")
        @Size(max = 120, message = "nome deve ter no máximo 120 caracteres")
        String nome,

        @NotBlank(message = "sexo é obrigatório")
        @Size(max = 30, message = "sexo deve ter no máximo 30 caracteres")
        String sexo,

        @NotBlank(message = "email é obrigatório")
        @Email(message = "email deve ser válido")
        @Size(max = 254, message = "email deve ter no máximo 254 caracteres")
        String email,

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

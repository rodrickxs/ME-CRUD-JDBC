package br.com.hotel.reservas.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Corpo do PUT /api/clientes/{id}. */
public record ClienteRequest(
        @NotBlank(message = "nome é obrigatório")
        @Size(max = 120, message = "nome deve ter no máximo 120 caracteres")
        String nome,

        @NotBlank(message = "sexo é obrigatório")
        @Size(max = 30, message = "sexo deve ter no máximo 30 caracteres")
        String sexo,

        @NotBlank(message = "email é obrigatório")
        @Email(message = "email deve ser válido")
        @Size(max = 254, message = "email deve ter no máximo 254 caracteres")
        String email
) {
}

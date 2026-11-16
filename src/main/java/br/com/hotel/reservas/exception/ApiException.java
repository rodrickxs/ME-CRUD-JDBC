package br.com.hotel.reservas.exception;

import org.springframework.http.HttpStatus;

/** Erro de negócio/infra que vira uma resposta HTTP com o status indicado. */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String mensagem) {
        super(mensagem);
        this.status = status;
    }

    public ApiException(HttpStatus status, String mensagem, Throwable causa) {
        super(mensagem, causa);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

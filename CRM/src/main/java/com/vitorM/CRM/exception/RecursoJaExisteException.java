package com.vitorM.CRM.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RecursoJaExisteException extends RuntimeException {

    public RecursoJaExisteException(String mensagem) {
        super(mensagem);
    }

    public RecursoJaExisteException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

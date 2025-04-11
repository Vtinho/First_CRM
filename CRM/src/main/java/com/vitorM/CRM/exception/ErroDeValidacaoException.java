package com.vitorM.CRM.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ErroDeValidacaoException extends RuntimeException {

    public ErroDeValidacaoException(String mensagem) {
        super(mensagem);
    }

    public ErroDeValidacaoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

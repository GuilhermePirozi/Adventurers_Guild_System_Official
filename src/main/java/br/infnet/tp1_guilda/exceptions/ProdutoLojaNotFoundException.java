package br.infnet.tp1_guilda.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProdutoLojaNotFoundException extends RuntimeException {

    public ProdutoLojaNotFoundException(String message) {
        super(message);
    }
}

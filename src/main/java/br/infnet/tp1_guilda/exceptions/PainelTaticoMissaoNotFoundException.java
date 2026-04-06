package br.infnet.tp1_guilda.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.time.OffsetDateTime;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class PainelTaticoMissaoNotFoundException extends RuntimeException {

    public PainelTaticoMissaoNotFoundException(OffsetDateTime inicio) {
        super("Nenhuma missão tática encontrada nos últimos 15 dias a partir de " + inicio + "!");
    }
}


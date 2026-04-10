package br.infnet.tp1_guilda.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Lançada quando a aplicação não consegue falar com o Elasticsearch (rede, cluster fora do ar,
 * timeout, etc.). Não é erro de regra de negócio: é falha de infraestrutura da busca.
 */
@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class ElasticsearchComunicacaoException extends RuntimeException {

    public ElasticsearchComunicacaoException(String message) {
        super(message);
    }
}

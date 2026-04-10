package br.infnet.tp1_guilda.dto.elastic;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ResponseFaixaPrecoLoja(
        String faixa,
        long quantidade,
        List<ResponseProdutoLoja> produtos
) {
}

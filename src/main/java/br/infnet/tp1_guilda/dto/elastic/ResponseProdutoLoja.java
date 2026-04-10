package br.infnet.tp1_guilda.dto.elastic;

import java.math.BigDecimal;

public record ResponseProdutoLoja(
        String nome,
        String descricao,
        String categoria,
        String raridade,
        BigDecimal preco
) {
}

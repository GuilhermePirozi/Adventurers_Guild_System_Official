package br.infnet.tp1_guilda.mapper;

import br.infnet.tp1_guilda.domain.elastic.ProdutoLoja;
import br.infnet.tp1_guilda.dto.elastic.ResponseFaixaPrecoLoja;
import br.infnet.tp1_guilda.dto.elastic.ResponseProdutoLoja;
import co.elastic.clients.elasticsearch._types.aggregations.RangeBucket;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class MapperProdutoLoja {

    public ResponseProdutoLoja toResponse(ProdutoLoja produto) {
        return new ResponseProdutoLoja(
                produto.getNome(),
                produto.getDescricao(),
                produto.getCategoria(),
                produto.getRaridade(),
                BigDecimal.valueOf(produto.getPreco())
        );
    }

    public ResponseFaixaPrecoLoja toFaixaPreco(RangeBucket bucket) {

        return new ResponseFaixaPrecoLoja(
                bucket.key(),
                bucket.docCount(),
                List.of()
        );
    }
}
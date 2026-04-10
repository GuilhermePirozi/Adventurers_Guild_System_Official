package br.infnet.tp1_guilda.service;

import br.infnet.tp1_guilda.domain.elastic.ProdutoLoja;
import br.infnet.tp1_guilda.dto.elastic.ResponseContagemPorCampo;
import br.infnet.tp1_guilda.dto.elastic.ResponseFaixaPrecoLoja;
import br.infnet.tp1_guilda.dto.elastic.ResponsePrecoMedioLoja;
import br.infnet.tp1_guilda.exceptions.ElasticsearchException;
import br.infnet.tp1_guilda.exceptions.ProdutoLojaNotFoundException;
import br.infnet.tp1_guilda.mapper.MapperProdutoLoja;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationRange;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProdutoLojaService {

    private static final String INDEX_LOJA = "guilda_loja";
    private static final int SEARCH_LIMIT = 100;

    private final ElasticsearchClient client;
    private final MapperProdutoLoja mapperProdutoLoja;

    public List<ProdutoLoja> buscarPorNome(String nome) {
        return executarBusca(
                Query.of(q -> q.match(m -> m.field("nome").query(nome))),
                "Nenhum produto encontrado com nome semelhante a \"" + nome + "\"."
        );
    }

    public List<ProdutoLoja> buscarPorDescricao(String descricao) {
        return executarBusca(
                Query.of(q -> q.match(m -> m.field("descricao").query(descricao))),
                "Nenhum produto encontrado com descrição contendo \"" + descricao + "\"."
        );
    }

    public List<ProdutoLoja> buscarPorFraseExata(String descricaoExata) {
        return executarBusca(
                Query.of(q -> q.matchPhrase(m -> m.field("descricao").query(descricaoExata))),
                "Nenhum produto encontrado com a frase exata \"" + descricaoExata + "\" na descrição."
        );
    }

    public List<ProdutoLoja> buscarPorNomeComTolerancia(String nome) {
        return executarBusca(
                Query.of(q -> q.fuzzy(f -> f.field("nome").value(nome).fuzziness("AUTO"))),
                "Nenhum produto encontrado para \"" + nome + "\" (busca fuzzy)."
        );
    }

    public List<ProdutoLoja> buscarPorNomeEDescricao(String termo) {
        return executarBusca(
                Query.of(q -> q.multiMatch(m -> m.fields(List.of("nome", "descricao")).query(termo))),
                "Nenhum produto encontrado para \"" + termo + "\" em nome/descrição."
        );
    }

    public List<ProdutoLoja> buscarPorDescricaoECategoria(String descricao, String categoria) {
        Query query = Query.of(q -> q.bool(b -> b
                .must(Query.of(m -> m.matchPhrase(mm -> mm.field("descricao").query(descricao))))
                .filter(Query.of(f -> f.term(t -> t.field("categoria").value(categoria))))
        ));

        return executarBusca(
                query,
                "Nenhum produto encontrado para termo \"" + descricao + "\" na categoria \"" + categoria + "\"."
        );
    }

    public List<ProdutoLoja> buscarPorFaixaPreco(BigDecimal min, BigDecimal max) {
        Query query = Query.of(q -> q.range(r -> r.number(n -> n
                .field("preco")
                .gte(min.doubleValue())
                .lte(max.doubleValue())
        )));
        return executarBusca(
                query,
                "Nenhum produto encontrado na faixa de preço de " + min + " até " + max + "."
        );
    }

    public List<ProdutoLoja> buscaCombinada(String categoria, String raridade, BigDecimal min, BigDecimal max) {
        Query query = Query.of(q -> q.bool(b -> b.filter(
                Query.of(f -> f.term(t -> t.field("categoria").value(categoria))),
                Query.of(f -> f.term(t -> t.field("raridade").value(raridade))),
                Query.of(r -> r.range(n -> n.number(nr -> nr
                        .field("preco")
                        .gte(min.doubleValue())
                        .lte(max.doubleValue())
                )))
        )));

        return executarBusca(
                query,
                "Nenhum produto encontrado com categoria \"" + categoria + "\", raridade \"" + raridade
                        + "\" e preço entre " + min + " e " + max + "."
        );
    }

    public List<ResponseContagemPorCampo> quantidadeProdutosPorCampo(String field) {
        String key = String.format("por_%s", field);

        List<ResponseContagemPorCampo> resultado = executarAgregacaoTerms(
                key,
                Aggregation.of(a -> a.terms(t -> t.field(field))),
                field
        );
        if (resultado.isEmpty()) {
            throw new ProdutoLojaNotFoundException("Nenhum produto encontrado para agregação por \"" + field + "\".");
        }
        return resultado;
    }

    public ResponsePrecoMedioLoja precoMedioProdutos() {
        try {
            SearchResponse<ProdutoLoja> response = client.search(s -> s
                            .index(INDEX_LOJA)
                            .size(0)
                            .aggregations("preco_medio", a -> a.avg(avg -> avg.field("preco")))
                    , ProdutoLoja.class);

            Double value = response.aggregations().get("preco_medio").avg().value();
            if (value == null) {
                throw new ProdutoLojaNotFoundException("Não foi possível calcular o preço médio (nenhum produto no índice).");
            }
            return new ResponsePrecoMedioLoja(value);
        } catch (IOException e) {
            throw new ElasticsearchException("Erro ao executar busca no Elasticsearch");
        }
    }

    public List<ResponseFaixaPrecoLoja> agruparEmFaixaPreco() {
        String aggregationKey = "faixa_preco";

        try {
            SearchResponse<ProdutoLoja> response = client.search(s -> s
                    .index(INDEX_LOJA)
                    .size(0)
                    .aggregations(aggregationKey, a -> a
                            .range(r -> r.field("preco").ranges(getRangesPreco()))
                    ), ProdutoLoja.class
            );

            List<ResponseFaixaPrecoLoja> faixas = response.aggregations()
                    .get(aggregationKey)
                    .range()
                    .buckets()
                    .array()
                    .stream()
                    .map(mapperProdutoLoja::toFaixaPreco)
                    .toList();
            if (faixas.isEmpty()) {
                throw new ProdutoLojaNotFoundException("Nenhum produto encontrado para agregação de faixas de preço.");
            }
            return faixas;
        } catch (IOException e) {
            throw new ElasticsearchException("Erro ao executar busca no Elasticsearch");
        }
    }

    private List<AggregationRange> getRangesPreco() {
        return List.of(
                AggregationRange.of(a -> a.to(100.0).key("Abaixo de 100")),
                AggregationRange.of(a -> a.from(100.0).to(300.0).key("De 100 a 300")),
                AggregationRange.of(a -> a.from(300.0).to(700.0).key("De 300 a 700")),
                AggregationRange.of(a -> a.from(700.0).key("Acima de 700"))
        );
    }

    private List<ProdutoLoja> executarBusca(Query query, String mensagemSeVazio) {
        try {
            SearchResponse<ProdutoLoja> response = client.search(s -> s
                            .index(INDEX_LOJA)
                            .size(SEARCH_LIMIT)
                            .query(query)
                            .sort(sort -> sort
                                    .field(f -> f
                                            .field("preco")
                                            .order(co.elastic.clients.elasticsearch._types.SortOrder.Asc)
                                    )
                            )
                    , ProdutoLoja.class);

            List<ProdutoLoja> itens = extrairHitSources(response);
            if (itens.isEmpty()) {
                throw new ProdutoLojaNotFoundException(mensagemSeVazio);
            }
            return itens;
        } catch (IOException e) {
            throw new ElasticsearchException("Erro ao executar busca no Elasticsearch");
        }
    }

    private List<ResponseContagemPorCampo> executarAgregacaoTerms(String key, Aggregation value, String field) {
        try {
            SearchResponse<ProdutoLoja> response = client.search(s -> s
                            .index(INDEX_LOJA)
                            .size(0)
                            .aggregations(key, value),
                    ProdutoLoja.class
            );

            return mapStringTermsAggregate(response, key, field);
        } catch (IOException e) {
            throw new ElasticsearchException("Erro ao executar busca no Elasticsearch");
        }
    }

    private List<ProdutoLoja> extrairHitSources(SearchResponse<ProdutoLoja> response) {
        if (response == null || response.hits() == null) {
            return Collections.emptyList();
        }
        return response.hits()
                .hits()
                .stream()
                .map(Hit::source)
                .filter(Objects::nonNull)
                .toList();
    }

    private List<ResponseContagemPorCampo> mapStringTermsAggregate(
            SearchResponse<ProdutoLoja> response,
            String key,
            String field
    ) {
        return response.aggregations()
                .get(key)
                .sterms()
                .buckets()
                .array()
                .stream()
                .map(bucket -> new ResponseContagemPorCampo(
                        field,
                        bucket.key().stringValue(),
                        bucket.docCount()
                ))
                .toList();
    }
}

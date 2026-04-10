package br.infnet.tp1_guilda.service;

import br.infnet.tp1_guilda.domain.elastic.ProdutoLoja;
import br.infnet.tp1_guilda.dto.elastic.ResponseContagemPorCampo;
import br.infnet.tp1_guilda.dto.elastic.ResponseFaixaPrecoLoja;
import br.infnet.tp1_guilda.dto.elastic.ResponsePrecoMedioLoja;
import br.infnet.tp1_guilda.exceptions.ElasticsearchComunicacaoException;
import br.infnet.tp1_guilda.mapper.MapperProdutoLoja;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.AggregationRange;
import co.elastic.clients.elasticsearch._types.query_dsl.FuzzyQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchPhraseQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.NumberRangeQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ProdutoLojaService {

    private final ElasticsearchClient client;
    private final MapperProdutoLoja mapperProdutoLoja;

    public List<ProdutoLoja> buscarPorNome(String nome) {
        return search(matchQuery("nome", nome)._toQuery());
    }

    public List<ProdutoLoja> buscarPorDescricao(String descricao) {
        return search(matchQuery("descricao", descricao)._toQuery());
    }

    public List<ProdutoLoja> buscarPorFraseExata(String descricaoExata) {
        return search(matchPhraseQuery("descricao", descricaoExata)._toQuery());
    }

    public List<ProdutoLoja> buscarPorNomeComTolerancia(String nome) {
        return search(fuzzyQuery("nome", nome)._toQuery());
    }

    public List<ProdutoLoja> buscarPorNomeEDescricao(String termo) {
        return search(multiMatchQuery(List.of("nome", "descricao"), termo)._toQuery());
    }

    public List<ProdutoLoja> buscarPorDescricaoECategoria(String descricao, String categoria) {
        return search(Query.of(q -> q
                .bool(b -> b
                        .must(matchQuery("descricao", descricao)._toQuery())
                        .filter(termQuery("categoria", categoria)._toQuery())
                )
        ));
    }

    public List<ProdutoLoja> buscarPorFaixaPreco(BigDecimal min, BigDecimal max) {
        return search(Query.of(q -> q
                .range(r -> r
                        .number(numberRangeQuery("preco", min, max))
                )
        ));
    }

    public List<ProdutoLoja> buscaCombinada(String categoria, String raridade, BigDecimal min, BigDecimal max) {
        return search(Query.of(q -> q
                .bool(b -> b
                        .filter(
                                termQuery("categoria", categoria)._toQuery(),
                                termQuery("raridade", raridade)._toQuery(),
                                Query.of(r -> r.range(n -> n.number(numberRangeQuery("preco", min, max))))
                        )
                )
        ));
    }

    public List<ResponseContagemPorCampo> quantidadeProdutosPorCampo(String field) {
        return search(
                String.format("por_%s", field),
                termsAggregation(field),
                field
        );
    }

    public ResponsePrecoMedioLoja precoMedioProdutos() {
        String key = "preco_medio";

        try {
            SearchResponse<ProdutoLoja> response = client.search(s -> s
                    .index("guilda_loja")
                    .size(0)
                    .aggregations(key, a -> a.avg(averageAggregation("preco")))
            , ProdutoLoja.class);

            return new ResponsePrecoMedioLoja(response.aggregations().get(key).avg().value());
        } catch (IOException e) {
            throw new ElasticsearchComunicacaoException("Erro ao executar busca no Elasticsearch");
        }
    }

    public List<ResponseFaixaPrecoLoja> agruparEmFaixaPreco() {
        String aggregationKey = "faixa_preco";

        try {
            SearchResponse<ProdutoLoja> response = client.search(s -> s
                    .index("guilda_loja")
                    .size(0)
                    .aggregations(aggregationKey, a -> a
                            .range(rangeAggregation("preco", getRangesPreco()))
                    ), ProdutoLoja.class
            );

            return response.aggregations()
                    .get(aggregationKey)
                    .range()
                    .buckets()
                    .array()
                    .stream()
                    .map(mapperProdutoLoja::toFaixaPreco)
                    .toList();
        } catch (IOException e) {
            throw new ElasticsearchComunicacaoException("Erro ao executar busca no Elasticsearch");
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

    private List<ProdutoLoja> search(Query query) {
        try {
            SearchResponse<ProdutoLoja> response = client.search(s -> s
                            .index("guilda_loja")
                            .size(100)
                            .query(query)
                            .sort(sort -> sort
                                    .field(f -> f
                                            .field("preco")
                                            .order(co.elastic.clients.elasticsearch._types.SortOrder.Asc)
                                    )
                            )
                    , ProdutoLoja.class);

            return extrairHitSources(response);
        } catch (IOException e) {
            throw new ElasticsearchComunicacaoException("Erro ao executar busca no Elasticsearch");
        }
    }

    private List<ResponseContagemPorCampo> search(String key, Aggregation value, String field) {
        try {
            SearchResponse<ProdutoLoja> response = client.search(s -> s
                            .index("guilda_loja")
                            .size(0)
                            .aggregations(key, value),
                    ProdutoLoja.class
            );

            return mapStringTermsAggregate(response, key, field);
        } catch (IOException e) {
            throw new ElasticsearchComunicacaoException("Erro ao executar busca no Elasticsearch");
        }
    }

    private List<ProdutoLoja> extrairHitSources(SearchResponse<ProdutoLoja> response) {
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

    private MatchQuery matchQuery(String field, String value) {
        return MatchQuery.of(q -> q
                .field(field)
                .query(value)
        );
    }

    private MatchPhraseQuery matchPhraseQuery(String field, String value) {
        return MatchPhraseQuery.of(q -> q
                .field(field)
                .query(value)
        );
    }

    private FuzzyQuery fuzzyQuery(String field, String value) {
        return FuzzyQuery.of(q -> q
                .field(field)
                .value(value)
                .fuzziness("AUTO")
        );
    }

    private MultiMatchQuery multiMatchQuery(List<String> fields, String value) {
        return MultiMatchQuery.of(q -> q
                .fields(fields)
                .query(value)
        );
    }

    private NumberRangeQuery numberRangeQuery(String field, BigDecimal min, BigDecimal max) {
        return NumberRangeQuery.of(q -> q
                .field(field)
                .gte(min.doubleValue())
                .lte(max.doubleValue())
        );
    }

    private TermQuery termQuery(String field, String value) {
        return TermQuery.of(q -> q
                .field(field)
                .value(value)
        );
    }

    private Aggregation termsAggregation(String field) {
        return Aggregation.of(a -> a
                .terms(t -> t.field(field))
        );
    }

    private co.elastic.clients.elasticsearch._types.aggregations.AverageAggregation averageAggregation(String field) {
        return co.elastic.clients.elasticsearch._types.aggregations.AverageAggregation.of(a -> a
                .field(field)
        );
    }

    private co.elastic.clients.elasticsearch._types.aggregations.RangeAggregation rangeAggregation(
            String field,
            List<AggregationRange> ranges
    ) {
        return Aggregation.of(a -> a
                        .range(r -> r
                                .field(field)
                                .ranges(ranges)
                        )
                )
                .range();
    }
}

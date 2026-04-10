package br.infnet.tp1_guilda.controllers;

import br.infnet.tp1_guilda.dto.elastic.ResponseContagemPorCampo;
import br.infnet.tp1_guilda.dto.elastic.ResponseFaixaPrecoLoja;
import br.infnet.tp1_guilda.dto.elastic.ResponsePrecoMedioLoja;
import br.infnet.tp1_guilda.dto.elastic.ResponseProdutoLoja;
import br.infnet.tp1_guilda.mapper.MapperProdutoLoja;
import br.infnet.tp1_guilda.service.ProdutoLojaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoLojaController {

    private final ProdutoLojaService produtoLojaService;
    private final MapperProdutoLoja mapperProdutoLoja;

    @GetMapping("/busca/nome")
    public ResponseEntity<List<ResponseProdutoLoja>> buscarPorNomeProduto(@RequestParam(name = "termo") String termo) {
        return ResponseEntity.ok(
                produtoLojaService.buscarPorNome(termo).stream().map(mapperProdutoLoja::toResponse).toList()
        );
    }

    @GetMapping("/busca/descricao")
    public ResponseEntity<List<ResponseProdutoLoja>> buscarPorDescricaoProduto(@RequestParam(name = "termo") String termo) {
        return ResponseEntity.ok(
                produtoLojaService.buscarPorDescricao(termo).stream().map(mapperProdutoLoja::toResponse).toList()
        );
    }

    @GetMapping("/busca/frase")
    public ResponseEntity<List<ResponseProdutoLoja>> buscarPorFraseExata(@RequestParam(name = "termo") String termo) {
        return ResponseEntity.ok(
                produtoLojaService.buscarPorFraseExata(termo).stream().map(mapperProdutoLoja::toResponse).toList()
        );
    }

    @GetMapping("/busca/fuzzy")
    public ResponseEntity<List<ResponseProdutoLoja>> buscarPorNomeComTolerancia(@RequestParam(value = "termo") String termo) {
        return ResponseEntity.ok(
                produtoLojaService.buscarPorNomeComTolerancia(termo).stream().map(mapperProdutoLoja::toResponse).toList()
        );
    }

    @GetMapping("/busca/multicampos")
    public ResponseEntity<List<ResponseProdutoLoja>> buscarPorNomeEDescricao(@RequestParam(name = "termo") String termo) {
        return ResponseEntity.ok(
                produtoLojaService.buscarPorNomeEDescricao(termo).stream().map(mapperProdutoLoja::toResponse).toList()
        );
    }

    @GetMapping("/busca/com-filtro")
    public ResponseEntity<List<ResponseProdutoLoja>> buscarPorDescricaoECategoria(
            @RequestParam(name = "termo") String termo,
            @RequestParam(name = "categoria") String categoria
    ) {
        return ResponseEntity.ok(
                produtoLojaService.buscarPorDescricaoECategoria(termo, categoria).stream()
                        .map(mapperProdutoLoja::toResponse)
                        .toList()
        );
    }

    @GetMapping("/busca/faixa-preco")
    public ResponseEntity<List<ResponseProdutoLoja>> buscarPorFaixaPreco(
            @RequestParam(name = "min", defaultValue = "0") BigDecimal min,
            @RequestParam(name = "max", defaultValue = "100") BigDecimal max
    ) {
        return ResponseEntity.ok(
                produtoLojaService.buscarPorFaixaPreco(min, max).stream().map(mapperProdutoLoja::toResponse).toList()
        );
    }

    @GetMapping("/busca/avancada")
    public ResponseEntity<List<ResponseProdutoLoja>> buscaCombinada(
            @RequestParam(name = "categoria") String categoria,
            @RequestParam(name = "raridade") String raridade,
            @RequestParam(name = "min") BigDecimal min,
            @RequestParam(name = "max") BigDecimal max
    ) {
        return ResponseEntity.ok(
                produtoLojaService.buscaCombinada(categoria, raridade, min, max).stream()
                        .map(mapperProdutoLoja::toResponse)
                        .toList()
        );
    }

    @GetMapping("/agregacoes/por-categoria")
    public ResponseEntity<List<ResponseContagemPorCampo>> quantidadeProdutosPorCategoria() {
        return ResponseEntity.ok(produtoLojaService.quantidadeProdutosPorCampo("categoria"));
    }

    @GetMapping("/agregacoes/por-raridade")
    public ResponseEntity<List<ResponseContagemPorCampo>> quantidadeProdutosPorRaridade() {
        return ResponseEntity.ok(produtoLojaService.quantidadeProdutosPorCampo("raridade"));
    }

    @GetMapping("/agregacoes/preco-medio")
    public ResponseEntity<ResponsePrecoMedioLoja> precoMedioProdutos() {
        return ResponseEntity.ok(produtoLojaService.precoMedioProdutos());
    }

    @GetMapping("/agregacoes/faixas-preco")
    public ResponseEntity<List<ResponseFaixaPrecoLoja>> agruparEmFaixaPreco() {
        return ResponseEntity.ok(produtoLojaService.agruparEmFaixaPreco());
    }
}

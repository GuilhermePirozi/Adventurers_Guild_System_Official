package br.infnet.tp1_guilda.controllers;

import br.infnet.tp1_guilda.domain.aventura.Aventureiro;
import br.infnet.tp1_guilda.dto.PageResult;
import br.infnet.tp1_guilda.dto.aventureiro.AtualizarAventureiro;
import br.infnet.tp1_guilda.dto.aventureiro.CriarAventureiro;
import br.infnet.tp1_guilda.dto.aventureiro.FilterRequestAventureiro;
import br.infnet.tp1_guilda.dto.aventureiro.ResponseAventureiro;
import br.infnet.tp1_guilda.dto.companheiro.AtualizarCompanheiro;
import br.infnet.tp1_guilda.dto.companheiro.DefinirCompanheiro;
import br.infnet.tp1_guilda.dto.consulta.aventureiro.ResponseAventureiroBusca;
import br.infnet.tp1_guilda.dto.consulta.aventureiro.ResponseAventureiroPerfil;
import br.infnet.tp1_guilda.enums.Classe;
import br.infnet.tp1_guilda.mapper.MapperAventureiro;
import br.infnet.tp1_guilda.service.AventureiroService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/aventureiros")
@RequiredArgsConstructor
@Validated
public class AventureiroController {

    private final MapperAventureiro mapperAventureiro;
    private final AventureiroService serviceAventureiro;

    @PostMapping
    public ResponseEntity<ResponseAventureiro> registrarAventureiro(@RequestBody @Valid CriarAventureiro dto) {
        Aventureiro salvo = serviceAventureiro.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapperAventureiro.toResponse(salvo));
    }

    @GetMapping
    public ResponseEntity<PageResult<ResponseAventureiro>> listar(
            @RequestParam(required = false) Classe classe,
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) @Min(value = 1, message = "O nível deve ser maior ou igual a 1") Integer nivelMinimo,
            @RequestParam(defaultValue = "nome") String ordenarPor,
            @RequestParam(defaultValue = "false") boolean ordemDecrescente,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "A página deve iniciar em 0") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Size deve ser entre 1 e 50") @Max(value = 100, message = "Size deve ser entre 1 e 50") int size
    ) {
        var filtro = new FilterRequestAventureiro(classe, ativo, nivelMinimo);
        var pagina = serviceAventureiro.listar(filtro, page, size, ordenarPor, ordemDecrescente);
        var dados = pagina.content().stream().map(mapperAventureiro::toResponse).toList();
        var body = new PageResult<>(pagina.page(), pagina.size(), pagina.total(), dados);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/busca")
    public ResponseEntity<PageResult<ResponseAventureiroBusca>> buscaPorNome(
            @RequestParam @NotBlank(message = "Informe um trecho do nome.") String trecho,
            @RequestParam(defaultValue = "nome") String ordenarPor,
            @RequestParam(defaultValue = "false") boolean ordemDecrescente,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "A página deve iniciar em 0") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Size deve ser entre 1 e 50") @Max(value = 50, message = "Size deve ser entre 1 e 50") int size
    ) {
        var pagina = serviceAventureiro.buscarPorNomeTrecho(trecho.trim(), page, size, ordenarPor, ordemDecrescente);
        var dados = pagina.content().stream().map(mapperAventureiro::toBusca).toList();
        var body = new PageResult<>(pagina.page(), pagina.size(), pagina.total(), dados);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}/perfil")
    public ResponseEntity<ResponseAventureiroPerfil> perfilCompleto(@PathVariable Long id) {
        return ResponseEntity.ok(serviceAventureiro.buscarPerfilCompleto(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseAventureiro> buscarPorId(@PathVariable Long id) {
        Aventureiro aventureiro = serviceAventureiro.buscarPorId(id);
        return ResponseEntity.ok(mapperAventureiro.toResponse(aventureiro));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseAventureiro> atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarAventureiro update
    ) {
        Aventureiro atualizado = serviceAventureiro.atualizar(id, update);
        return ResponseEntity.ok(mapperAventureiro.toResponse(atualizado));
    }

    @PatchMapping("/{id}/encerrar-vinculo")
    public ResponseEntity<ResponseAventureiro> encerrarVinculo(@PathVariable Long id) {
        Aventureiro aventureiro = serviceAventureiro.encerrarVinculo(id);
        return ResponseEntity.ok(mapperAventureiro.toResponse(aventureiro));
    }

    @PatchMapping("/{id}/recrutar")
    public ResponseEntity<ResponseAventureiro> recrutarNovamente(@PathVariable Long id) {
        Aventureiro aventureiro = serviceAventureiro.recrutarNovamente(id);
        return ResponseEntity.ok(mapperAventureiro.toResponse(aventureiro));
    }

    @PutMapping("/{id}/companheiro")
    public ResponseEntity<ResponseAventureiro> definirCompanheiro(@PathVariable Long id, @Valid @RequestBody DefinirCompanheiro dto) {
        Aventureiro aventureiro = serviceAventureiro.definirCompanheiro(id, dto);
        return ResponseEntity.ok(mapperAventureiro.toResponse(aventureiro));
    }

    @PatchMapping("/{id}/companheiro")
    public ResponseEntity<ResponseAventureiro> atualizarCompanheiro(@PathVariable Long id, @Valid @RequestBody AtualizarCompanheiro dto) {
        Aventureiro aventureiro = serviceAventureiro.atualizarCompanheiro(id, dto);
        return ResponseEntity.ok(mapperAventureiro.toResponse(aventureiro));
    }

    @PatchMapping("/{id}/remover-companheiro")
    public ResponseEntity<ResponseAventureiro> removerCompanheiro(@PathVariable Long id) {
        Aventureiro aventureiro = serviceAventureiro.removerCompanheiro(id);
        return ResponseEntity.ok(mapperAventureiro.toResponse(aventureiro));
    }
}

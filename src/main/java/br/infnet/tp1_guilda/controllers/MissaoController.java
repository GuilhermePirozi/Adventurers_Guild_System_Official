package br.infnet.tp1_guilda.controllers;

import br.infnet.tp1_guilda.domain.aventura.Missao;
import br.infnet.tp1_guilda.domain.aventura.ParticipacaoMissao;
import br.infnet.tp1_guilda.domain.aventura.enums.NivelPerigo;
import br.infnet.tp1_guilda.domain.aventura.enums.StatusMissao;
import br.infnet.tp1_guilda.dto.PageResult;
import br.infnet.tp1_guilda.dto.consulta.missao.FilterConsultaMissao;
import br.infnet.tp1_guilda.dto.consulta.missao.ResponseMissaoDetalhe;
import br.infnet.tp1_guilda.dto.operacoes.ResponsePainelTaticoMissao;
import br.infnet.tp1_guilda.dto.missao.AtualizarMissao;
import br.infnet.tp1_guilda.dto.missao.CriarMissao;
import br.infnet.tp1_guilda.dto.missao.ResponseMissao;
import br.infnet.tp1_guilda.dto.participacaoMissao.CriarParticipacaoMissao;
import br.infnet.tp1_guilda.dto.participacaoMissao.ResponseParticipacaoMissao;
import br.infnet.tp1_guilda.mapper.MapperMissao;
import br.infnet.tp1_guilda.mapper.MapperParticipacaoMissao;
import br.infnet.tp1_guilda.service.MissaoService;
import br.infnet.tp1_guilda.service.PainelTaticoService;
import br.infnet.tp1_guilda.service.ParticipacaoMissaoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/missoes")
@RequiredArgsConstructor
@Validated
public class MissaoController {

    private final MapperMissao mapperMissao;
    private final MissaoService missaoService;
    private final ParticipacaoMissaoService participacaoMissaoService;
    private final MapperParticipacaoMissao mapperParticipacaoMissao;
    private final PainelTaticoService painelTaticoService;

    @PostMapping
    public ResponseEntity<ResponseMissao> criar(@RequestBody @Valid CriarMissao dto) {
        Missao salva = missaoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapperMissao.toResponse(salva));
    }

    @GetMapping
    public ResponseEntity<PageResult<ResponseMissao>> listar(
            @RequestParam(required = false) StatusMissao status,
            @RequestParam(required = false) NivelPerigo nivelPerigo,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dataCriacaoDe,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime dataCriacaoAte,
            @RequestParam(defaultValue = "titulo") String ordenarPor,
            @RequestParam(defaultValue = "false") boolean ordemDecrescente,
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "A página deve iniciar em 0") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Size deve ser entre 1 e 50") @Max(value = 50, message = "Size deve ser entre 1 e 50") int size
    ) {
        var filtro = new FilterConsultaMissao(status, nivelPerigo, dataCriacaoDe, dataCriacaoAte);
        var pagina = missaoService.consultar(filtro, page, size, ordenarPor, ordemDecrescente);
        var dados = pagina.content().stream().map(mapperMissao::toResponse).toList();
        var body = new PageResult<>(pagina.page(), pagina.size(), pagina.total(), dados);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/{id}/detalhe")
    public ResponseEntity<ResponseMissaoDetalhe> detalhar(@PathVariable Long id) {
        Missao missao = missaoService.buscarDetalhado(id);
        return ResponseEntity.ok(mapperMissao.toDetalhe(missao));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseMissao> buscarPorId(@PathVariable Long id) {
        Missao missao = missaoService.buscarPorId(id);
        return ResponseEntity.ok(mapperMissao.toResponse(missao));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResponseMissao> atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarMissao dto) {
        Missao atualizada = missaoService.atualizar(id, dto);
        return ResponseEntity.ok(mapperMissao.toResponse(atualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        missaoService.remover(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{missaoId}/aventureiros/{aventureiroId}/participacao")
    public ResponseEntity<ResponseParticipacaoMissao> registrarParticipacao(
            @PathVariable Long missaoId,
            @PathVariable Long aventureiroId,
            @RequestBody @Valid CriarParticipacaoMissao dto
    ) {
        ParticipacaoMissao salva = participacaoMissaoService.participar(missaoId, aventureiroId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapperParticipacaoMissao.toResponse(salva));
    }

    @DeleteMapping("/{missaoId}/aventureiros/{aventureiroId}/participacao")
    public ResponseEntity<Void> removerParticipacao(@PathVariable Long missaoId,@PathVariable Long aventureiroId) {
        participacaoMissaoService.removerParticipacao(missaoId, aventureiroId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/top15dias")
    public List<ResponsePainelTaticoMissao> top15Dias() {
        return painelTaticoService.buscarMissoesRelevantes();
    }
}

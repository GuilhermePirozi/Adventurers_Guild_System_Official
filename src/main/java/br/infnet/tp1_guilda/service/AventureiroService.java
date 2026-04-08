package br.infnet.tp1_guilda.service;

import br.infnet.tp1_guilda.domain.aventura.Aventureiro;
import br.infnet.tp1_guilda.domain.aventura.Companheiro;
import br.infnet.tp1_guilda.repository.audit.RepositoryOrganization;
import br.infnet.tp1_guilda.repository.audit.RepositoryUser;
import br.infnet.tp1_guilda.repository.aventura.RepositoryAventureiro;
import br.infnet.tp1_guilda.repository.aventura.RepositoryParticipacaoMissao;
import br.infnet.tp1_guilda.exceptions.AventureiroNotFoundException;
import br.infnet.tp1_guilda.exceptions.BusinessException;
import br.infnet.tp1_guilda.exceptions.CompanheiroNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.infnet.tp1_guilda.dto.PageResult;
import br.infnet.tp1_guilda.dto.aventureiro.AtualizarAventureiro;
import br.infnet.tp1_guilda.dto.aventureiro.CriarAventureiro;
import br.infnet.tp1_guilda.dto.consulta.aventureiro.ResponseAventureiroPerfil;
import br.infnet.tp1_guilda.dto.consulta.aventureiro.UltimaMissaoParticipacaoResumo;
import br.infnet.tp1_guilda.dto.aventureiro.FilterRequestAventureiro;
import br.infnet.tp1_guilda.dto.companheiro.AtualizarCompanheiro;
import br.infnet.tp1_guilda.dto.companheiro.DefinirCompanheiro;
import br.infnet.tp1_guilda.mapper.MapperAventureiro;
import br.infnet.tp1_guilda.mapper.MapperCompanheiro;

@Service
@RequiredArgsConstructor
public class AventureiroService {

    private final RepositoryParticipacaoMissao repositoryParticipacaoMissao;
    private final RepositoryAventureiro repositoryAventureiro;
    private final RepositoryOrganization repositoryOrganization;
    private final RepositoryUser repositoryUser;
    private final MapperAventureiro mapperAventureiro;
    private final MapperCompanheiro mapperCompanheiro;

    public Aventureiro criar(CriarAventureiro dto) {
        var organizacao = repositoryOrganization.findById(dto.organizacaoId())
                .orElseThrow(() -> new BusinessException("Organização não encontrada."));
        var user = repositoryUser.findById(dto.usuarioId())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado."));
        Aventureiro aventureiro = mapperAventureiro.toEntity(organizacao, user, dto);
        return repositoryAventureiro.save(aventureiro);
    }

    public Aventureiro buscarPorId(Long id) {
        return repositoryAventureiro.findById(id)
                .orElseThrow(() -> new AventureiroNotFoundException(id));
    }

    public Aventureiro atualizar(Long id, AtualizarAventureiro update) {

        Aventureiro aventureiro = buscarPorId(id);

        if (update.nome() != null) {
            if (update.nome().isBlank()) {
                throw new BusinessException("O nome do aventureiro não pode ser vazio.");
            }
            aventureiro.alterarNome(update.nome());
        }

        if (update.classe() != null) {
            aventureiro.alterarClasse(update.classe());
        }

        if (update.nivel() != null) {
            aventureiro.alterarNivel(update.nivel());
        }

        return repositoryAventureiro.save(aventureiro);
    }

    public Aventureiro encerrarVinculo(Long id) {
        Aventureiro aventureiro = buscarPorId(id);

        if (!aventureiro.getAtivo()) {
            throw new BusinessException("O aventureiro já está inativo.");
        }

        aventureiro.encerrarVinculo();
        return repositoryAventureiro.save(aventureiro);
    }

    public Aventureiro recrutarNovamente(Long id) {
        Aventureiro aventureiro = buscarPorId(id);

        if (aventureiro.getAtivo()) {
            throw new BusinessException("O aventureiro já está ativo.");
        }

        aventureiro.recrutar();
        return repositoryAventureiro.save(aventureiro);
    }

    public Aventureiro removerCompanheiro(Long id) {
        Aventureiro aventureiro = buscarPorId(id);

        if (aventureiro.getCompanheiro() == null) {
            throw new BusinessException("O aventureiro não possui companheiro para remover.");
        }

        aventureiro.removerCompanheiro();
        return repositoryAventureiro.save(aventureiro);
    }

    @Transactional(readOnly = true)
    public PageResult<Aventureiro> listar(
            FilterRequestAventureiro filtro,
            int page,
            int size,
            String ordenarPor,
            boolean ordemDecrescente
    ) {
        Sort.Direction dir = ordemDecrescente ? Sort.Direction.DESC : Sort.Direction.ASC;
        String campo = "nivel".equalsIgnoreCase(ordenarPor) ? "nivel" : "nome";
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, campo));
        Page<Aventureiro> resultado = repositoryAventureiro.consultarComFiltro(
                filtro.classe(),
                filtro.ativo(),
                filtro.nivelMinimo(),
                pageable
        );
        return new PageResult<>(
                resultado.getNumber(),
                resultado.getSize(),
                (int) resultado.getTotalElements(),
                resultado.getContent()
        );
    }

    @Transactional(readOnly = true)
    public PageResult<Aventureiro> buscarPorNomeTrecho(
            String trecho,
            int page,
            int size,
            String ordenarPor,
            boolean ordemDecrescente
    ) {
        Sort.Direction dir = ordemDecrescente ? Sort.Direction.DESC : Sort.Direction.ASC;
        String campo = "nivel".equalsIgnoreCase(ordenarPor) ? "nivel" : "nome";
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, campo));
        Page<Aventureiro> resultado = repositoryAventureiro.buscarPorNomeContendo(trecho, pageable);
        return new PageResult<>(
                resultado.getNumber(),
                resultado.getSize(),
                (int) resultado.getTotalElements(),
                resultado.getContent()
        );
    }

    @Transactional(readOnly = true)
    public ResponseAventureiroPerfil buscarPerfilCompleto(Long id) {
        Aventureiro aventureiro = buscarPorId(id);
        long total = repositoryParticipacaoMissao.countByAventureiro_Id(id);
        var companheiroDto = aventureiro.getCompanheiro() != null
                ? mapperCompanheiro.toResponse(aventureiro.getCompanheiro())
                : null;
        UltimaMissaoParticipacaoResumo ultima = repositoryParticipacaoMissao
                .findFirstByAventureiro_IdOrderByDataRegistroDesc(id)
                .map(p -> new UltimaMissaoParticipacaoResumo(
                        p.getMissao().getId(),
                        p.getMissao().getTitulo(),
                        p.getDataRegistro()
                ))
                .orElse(null);
        Long orgId = aventureiro.getOrganizacao() != null ? aventureiro.getOrganizacao().getId() : null;
        return new ResponseAventureiroPerfil(
                aventureiro.getId(),
                orgId,
                aventureiro.getNome(),
                aventureiro.getClasse(),
                aventureiro.getNivel(),
                Boolean.TRUE.equals(aventureiro.getAtivo()),
                companheiroDto,
                total,
                ultima
        );
    }

    public Aventureiro definirCompanheiro(Long id, DefinirCompanheiro dto) {
        Aventureiro aventureiro = buscarPorId(id);

        if (aventureiro.getCompanheiro() != null) {
            throw new BusinessException("O aventureiro já possui um companheiro.");
        }

        Companheiro companheiro = mapperCompanheiro.toEntity(dto);
        companheiro.setAventureiro(aventureiro);
        aventureiro.definirCompanheiro(companheiro);

        return repositoryAventureiro.save(aventureiro);
    }

    public Aventureiro atualizarCompanheiro(Long id, AtualizarCompanheiro dto) {
        Aventureiro aventureiro = buscarPorId(id);
        if (aventureiro.getCompanheiro() == null) {
            throw new CompanheiroNotFoundException(id);
        }
        Companheiro companheiro = aventureiro.getCompanheiro();
        if (dto.nome() != null) {
            if (dto.nome().isBlank()) {
                throw new BusinessException("O nome do companheiro não pode ser vazio.");
            }
            companheiro.setNome(dto.nome());
        }
        if (dto.especie() != null) {
            companheiro.setEspecie(dto.especie());
        }
        if (dto.lealdade() != null) {
            companheiro.setLealdade(dto.lealdade());
        }
        return repositoryAventureiro.save(aventureiro);
    }
}
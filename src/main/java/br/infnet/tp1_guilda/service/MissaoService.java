package br.infnet.tp1_guilda.service;

import br.infnet.tp1_guilda.domain.aventura.Missao;
import br.infnet.tp1_guilda.dto.PageResult;
import br.infnet.tp1_guilda.dto.consulta.missao.FilterConsultaMissao;
import br.infnet.tp1_guilda.dto.missao.AtualizarMissao;
import br.infnet.tp1_guilda.dto.missao.CriarMissao;
import br.infnet.tp1_guilda.exceptions.BusinessException;
import br.infnet.tp1_guilda.exceptions.MissaoNotFoundException;
import br.infnet.tp1_guilda.mapper.MapperMissao;
import br.infnet.tp1_guilda.repository.audit.OrganizationRepository;
import br.infnet.tp1_guilda.repository.aventura.RepositoryMissao;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class MissaoService {

    private final RepositoryMissao repository;
    private final OrganizationRepository organizationRepository;
    private final MapperMissao mapperMissao;

    public Missao criar(CriarMissao dto) {
        var organizacao = organizationRepository.findById(dto.organizacaoId())
                .orElseThrow(() -> new BusinessException("Organização não encontrada."));
        Missao missao = mapperMissao.toEntity(organizacao, dto);
        validarMissaoParaCriacao(missao);
        return repository.save(missao);
    }

    public Missao buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new MissaoNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Missao buscarDetalhado(Long id) {
        return repository.findDetalhadoById(id)
                .orElseThrow(() -> new MissaoNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public PageResult<Missao> consultar(
            FilterConsultaMissao filtro,
            int page,
            int size,
            String ordenarPor,
            boolean ordemDecrescente
    ) {
        Sort.Direction dir = ordemDecrescente ? Sort.Direction.DESC : Sort.Direction.ASC;
        String campo = resolverCampoOrdenacaoMissao(ordenarPor);
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, campo));

        OffsetDateTime dataDe = filtro.dataCriacaoDe() != null ? filtro.dataCriacaoDe()
        : OffsetDateTime.parse("1900-01-01T00:00:00Z");

        OffsetDateTime dataAte = filtro.dataCriacaoAte() != null ? filtro.dataCriacaoAte()
                : OffsetDateTime.now();

        Page<Missao> resultado = repository.consultarComFiltro(
                filtro.status(),
                filtro.nivelPerigo(),
                dataDe,
                dataAte,
                pageable
        );

        return new PageResult<>(
                resultado.getNumber(),
                resultado.getSize(),
                (int) resultado.getTotalElements(),
                resultado.getContent()
        );
    }

    private static String resolverCampoOrdenacaoMissao(String ordenarPor) {
        if (ordenarPor == null) {
            return "titulo";
        }
        return switch (ordenarPor.toLowerCase()) {
            case "datacriacao", "data_criacao" -> "dataCriacao";
            case "status" -> "status";
            case "nivelperigo", "nivel_perigo" -> "nivelPerigo";
            default -> "titulo";
        };
    }

    public Missao atualizar(Long id, AtualizarMissao dto) {
        Missao missao = buscarPorId(id);

        if (dto.titulo() != null) {
            if (dto.titulo().isBlank()) {
                throw new BusinessException("O título da missão é obrigatório.");
            }
            if (dto.titulo().length() > 150) {
                throw new BusinessException("O título da missão deve ter no máximo 150 caracteres.");
            }
        }

        mapperMissao.aplicarAtualizacao(missao, dto);
        validarDatas(missao);
        return repository.save(missao);
    }

    public void remover(Long id) {
        Missao missao = buscarPorId(id);
        repository.delete(missao);
    }

    private void validarMissaoParaCriacao(Missao missao) {
        if (missao == null) {
            throw new BusinessException("A missão é obrigatória.");
        }

        if (missao.getOrganizacao() == null) {
            throw new BusinessException("A missão deve estar vinculada a uma organização.");
        }

        if (missao.getTitulo() == null || missao.getTitulo().isBlank()) {
            throw new BusinessException("O título da missão é obrigatório.");
        }

        if (missao.getTitulo().length() > 150) {
            throw new BusinessException("O título da missão deve ter no máximo 150 caracteres.");
        }

        if (missao.getNivelPerigo() == null) {
            throw new BusinessException("O nível de perigo é obrigatório.");
        }

        if (missao.getStatus() == null) {
            throw new BusinessException("O status da missão é obrigatório.");
        }

        validarDatas(missao);
    }

    private void validarDatas(Missao missao) {
        if (missao.getDataInicio() != null && missao.getDataTermino() != null
                && missao.getDataTermino().isBefore(missao.getDataInicio())) {
            throw new BusinessException("A data de término não pode ser anterior à data de início.");
        }
    }
}

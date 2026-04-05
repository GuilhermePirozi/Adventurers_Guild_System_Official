package br.infnet.tp1_guilda.service;

import br.infnet.tp1_guilda.domain.operacoes.PainelTaticoMissao;
import br.infnet.tp1_guilda.repository.operacoes.RepositoryPainelTaticoMissao;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class PainelTaticoService {

    private final RepositoryPainelTaticoMissao repository;

    public PainelTaticoService(RepositoryPainelTaticoMissao repository) {
        this.repository = repository;
    }

    public List<PainelTaticoMissao> buscarMissoesRelevantes() {
        return repository.findTop10ByUltimaAtualizacaoAfterOrderByIndiceProntidaoDesc(OffsetDateTime.now().minusDays(15));
    }
}

package br.infnet.tp1_guilda.service;

import br.infnet.tp1_guilda.domain.operacoes.PainelTaticoMissao;
import br.infnet.tp1_guilda.dto.operacoes.ResponsePainelTaticoMissao;
import br.infnet.tp1_guilda.repository.operacoes.RepositoryPainelTaticoMissao;
import br.infnet.tp1_guilda.exceptions.PainelTaticoMissaoNotFoundException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class PainelTaticoService {

    private final RepositoryPainelTaticoMissao repository;

    public PainelTaticoService(RepositoryPainelTaticoMissao repository) {
        this.repository = repository;
    }

    public List<ResponsePainelTaticoMissao> buscarMissoesRelevantes() {
        OffsetDateTime inicio = OffsetDateTime.now().minusDays(15);
        List<PainelTaticoMissao> resultado = repository.findTop10ByUltimaAtualizacaoAfterOrderByIndiceProntidaoDesc(inicio);

        if (resultado.isEmpty()) {
            throw new PainelTaticoMissaoNotFoundException(inicio);
        }

        return resultado.stream()
                .map(p -> new ResponsePainelTaticoMissao(
                        p.getMissaoId(),
                        p.getTitulo(),
                        p.getStatus(),
                        p.getNivelPerigo(),
                        p.getOrganizacaoId(),
                        p.getTotalParticipantes(),
                        p.getNivelMedioEquipe(),
                        p.getTotalRecompensa(),
                        p.getTotalMvps(),
                        p.getParticipantesComCompanheiro(),
                        p.getUltimaAtualizacao(),
                        p.getIndiceProntidao()
                ))
                .toList();
    }
}

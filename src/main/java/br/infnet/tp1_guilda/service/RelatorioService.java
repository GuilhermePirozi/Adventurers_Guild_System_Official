package br.infnet.tp1_guilda.service;

import br.infnet.tp1_guilda.domain.aventura.enums.StatusMissao;
import br.infnet.tp1_guilda.dto.relatorio.ResponseMissaoMetricas;
import br.infnet.tp1_guilda.dto.relatorio.ResponseRankingParticipacao;
import br.infnet.tp1_guilda.exceptions.BusinessException;
import br.infnet.tp1_guilda.mapper.MapperRelatorio;
import br.infnet.tp1_guilda.repository.aventura.RepositoryParticipacaoMissao;
import br.infnet.tp1_guilda.repository.aventura.RepositoryParticipacaoMissao.MissaoMetricasProj;
import br.infnet.tp1_guilda.repository.aventura.RepositoryParticipacaoMissao.RankingParticipacaoProj;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioService {

    private final RepositoryParticipacaoMissao repositoryParticipacaoMissao;
    private final MapperRelatorio mapperRelatorio;

    @Transactional(readOnly = true)
    public List<ResponseRankingParticipacao> rankingParticipacao(OffsetDateTime inicio,OffsetDateTime fim,StatusMissao statusMissao
    ) {
        validarPeriodo(inicio, fim);
        return repositoryParticipacaoMissao.rankingPorParticipacao(inicio, fim, statusMissao).stream()
                .sorted(comparadorRankingParticipacao())
                .map(mapperRelatorio::toRankingParticipacao)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ResponseMissaoMetricas> metricasMissoesNoPeriodo(OffsetDateTime inicio, OffsetDateTime fim) {
        validarPeriodo(inicio, fim);
        return repositoryParticipacaoMissao.metricasPorMissaoNoPeriodo(inicio, fim).stream()
                .sorted(comparadorTituloMissao())
                .map(mapperRelatorio::toMissaoMetricas)
                .toList();
    }

    private static Comparator<RankingParticipacaoProj> comparadorRankingParticipacao() {
        return Comparator
                .comparing((RankingParticipacaoProj r) -> r.getTotalParticipacoes() != null ? r.getTotalParticipacoes() : 0L)
                .reversed()
                .thenComparing(r -> r.getSomaRecompensasOuro() != null ? r.getSomaRecompensasOuro() : 0L, Comparator.reverseOrder())
                .thenComparing(r -> r.getQuantidadeDestaques() != null ? r.getQuantidadeDestaques() : 0L, Comparator.reverseOrder());
    }

    private static Comparator<MissaoMetricasProj> comparadorTituloMissao() {
        return Comparator.comparing(
                m -> m.getTitulo() != null ? m.getTitulo() : "",
                String.CASE_INSENSITIVE_ORDER
        );
    }

    private static void validarPeriodo(OffsetDateTime inicio, OffsetDateTime fim) {
        if (inicio == null || fim == null) {
            throw new BusinessException("Informe início e fim do período.");
        }
        if (inicio.isAfter(fim)) {
            throw new BusinessException("A data inicial não pode ser depois da data final.");
        }
    }

}

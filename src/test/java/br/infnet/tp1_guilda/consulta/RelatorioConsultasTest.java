package br.infnet.tp1_guilda.consulta;

import br.infnet.tp1_guilda.domain.aventura.Aventureiro;
import br.infnet.tp1_guilda.domain.aventura.Missao;
import br.infnet.tp1_guilda.domain.aventura.ParticipacaoMissao;
import br.infnet.tp1_guilda.domain.aventura.enums.NivelPerigo;
import br.infnet.tp1_guilda.domain.aventura.enums.PapelMissao;
import br.infnet.tp1_guilda.domain.aventura.enums.StatusMissao;
import br.infnet.tp1_guilda.enums.Classe;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RelatorioConsultasTest extends ConsultasTestBase {

    @Test
    @DisplayName("Ranking de participação: agrega totais por aventureiro no período (consulta do relatório)")
    void rankingParticipacao_porPeriodo() {
        OffsetDateTime inicio = OffsetDateTime.parse("2020-01-01T00:00:00Z");
        OffsetDateTime fim = OffsetDateTime.parse("2030-12-31T23:59:59Z");

        Aventureiro a1 = repositoryAventureiro.save(newAventureiro("Rank A", Classe.GUERREIRO, 5));
        Aventureiro a2 = repositoryAventureiro.save(newAventureiro("Rank B", Classe.MAGO, 5));
        Missao m1 = repositoryMissao.save(new Missao(organizacao, "Missão rank 1", NivelPerigo.BAIXO, StatusMissao.CONCLUIDA));
        Missao m2 = repositoryMissao.save(new Missao(organizacao, "Missão rank 2", NivelPerigo.BAIXO, StatusMissao.CONCLUIDA));
        Missao m3 = repositoryMissao.save(new Missao(organizacao, "Missão rank 3", NivelPerigo.BAIXO, StatusMissao.CONCLUIDA));

        repositoryParticipacaoMissao.save(new ParticipacaoMissao(m1, a1, PapelMissao.ATAQUE, 10, true));
        repositoryParticipacaoMissao.save(new ParticipacaoMissao(m2, a1, PapelMissao.DEFESA, 20, false));
        repositoryParticipacaoMissao.save(new ParticipacaoMissao(m3, a2, PapelMissao.LIDER, 5, false));

        var linhas = relatorioService.rankingParticipacao(inicio, fim, null);
        assertEquals(2, linhas.size());
        assertEquals(a1.getId(), linhas.getFirst().aventureiroId());
        assertEquals(2L, linhas.getFirst().totalParticipacoes());
        assertEquals(30L, linhas.getFirst().somaRecompensasOuro());
        assertEquals(1L, linhas.getFirst().quantidadeDestaques());
    }

    @Test
    @DisplayName("Relatório de missões com métricas: participantes e soma de recompensas por missão no período")
    void missoesMetricas_noPeriodo() {
        OffsetDateTime inicio = OffsetDateTime.parse("2020-01-01T00:00:00Z");
        OffsetDateTime fim = OffsetDateTime.parse("2030-12-31T23:59:59Z");

        Aventureiro a1 = repositoryAventureiro.save(newAventureiro("Métricas 1", Classe.GUERREIRO, 3));
        Aventureiro a2 = repositoryAventureiro.save(newAventureiro("Métricas 2", Classe.LADINO, 3));
        Missao m = repositoryMissao.save(new Missao(organizacao, "Operação X", NivelPerigo.ALTO, StatusMissao.EM_ANDAMENTO));

        repositoryParticipacaoMissao.save(new ParticipacaoMissao(m, a1, PapelMissao.ATAQUE, 40, false));
        repositoryParticipacaoMissao.save(new ParticipacaoMissao(m, a2, PapelMissao.SUPORTE, 60, true));

        var linhas = relatorioService.metricasMissoesNoPeriodo(inicio, fim);
        var linhaM = linhas.stream().filter(l -> m.getId().equals(l.missaoId())).findFirst().orElseThrow();
        assertEquals(2L, linhaM.quantidadeParticipantes());
        assertEquals(100L, linhaM.totalRecompensasDistribuidas());
        assertEquals(StatusMissao.EM_ANDAMENTO, linhaM.status());
    }
}

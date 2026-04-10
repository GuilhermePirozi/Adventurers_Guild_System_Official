package br.infnet.tp1_guilda.operacoes;

import br.infnet.tp1_guilda.config.TestCacheConfig;
import br.infnet.tp1_guilda.domain.aventura.enums.NivelPerigo;
import br.infnet.tp1_guilda.domain.aventura.enums.StatusMissao;
import br.infnet.tp1_guilda.domain.operacoes.PainelTaticoMissao;
import br.infnet.tp1_guilda.repository.operacoes.RepositoryPainelTaticoMissao;
import br.infnet.tp1_guilda.service.PainelTaticoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
@ActiveProfiles("test")
@Import({TestCacheConfig.class, PainelTaticoService.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EntityScan(basePackages = "br.infnet.tp1_guilda.domain.operacoes")
@EnableJpaRepositories(basePackages = "br.infnet.tp1_guilda.repository.operacoes")
class PainelTaticoServiceTest {

    @Autowired
    RepositoryPainelTaticoMissao repositoryPainelTaticoMissao;

    @Autowired
    PainelTaticoService painelTaticoService;

    @Test
    @DisplayName("Top missões (15 dias): PainelTaticoService busca no repositório e ordena por índice de prontidão")
    void buscarMissoesRelevantes_executaServiceERetornaRanking() {
        OffsetDateTime agora = OffsetDateTime.now();

        repositoryPainelTaticoMissao.save(linhaPainel(1L, "Missão Beta", new BigDecimal("70.0"), agora));
        repositoryPainelTaticoMissao.save(linhaPainel(2L, "Missão Alfa", new BigDecimal("95.5"), agora));

        var resultado = painelTaticoService.buscarMissoesRelevantes();

        assertFalse(resultado.isEmpty());
        assertEquals(2, resultado.size());
        assertEquals("Missão Alfa", resultado.getFirst().titulo());
        assertEquals(0, new BigDecimal("95.5").compareTo(resultado.getFirst().indiceProntidao()));
    }

    private static PainelTaticoMissao linhaPainel(long missaoId, String titulo, BigDecimal indiceProntidao, OffsetDateTime ultimaAtualizacao) {
        PainelTaticoMissao p = new PainelTaticoMissao();
        p.setMissaoId(missaoId);
        p.setTitulo(titulo);
        p.setStatus(StatusMissao.EM_ANDAMENTO);
        p.setNivelPerigo(NivelPerigo.MEDIO);
        p.setOrganizacaoId(1L);
        p.setTotalParticipantes(2);
        p.setNivelMedioEquipe(new BigDecimal("5.0"));
        p.setTotalRecompensa(new BigDecimal("100.00"));
        p.setTotalMvps(1);
        p.setParticipantesComCompanheiro(0);
        p.setUltimaAtualizacao(ultimaAtualizacao);
        p.setIndiceProntidao(indiceProntidao);
        return p;
    }
}

package br.infnet.tp1_guilda.consulta;

import br.infnet.tp1_guilda.domain.aventura.Aventureiro;
import br.infnet.tp1_guilda.domain.aventura.Missao;
import br.infnet.tp1_guilda.domain.aventura.ParticipacaoMissao;
import br.infnet.tp1_guilda.domain.aventura.enums.NivelPerigo;
import br.infnet.tp1_guilda.domain.aventura.enums.PapelMissao;
import br.infnet.tp1_guilda.domain.aventura.enums.StatusMissao;
import br.infnet.tp1_guilda.dto.consulta.missao.FilterConsultaMissao;
import br.infnet.tp1_guilda.enums.Classe;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MissaoConsultasTest extends ConsultasTestBase {

    @Test
    @DisplayName("Listagem de missões: filtro por status (e intervalo de datas padrão do serviço)")
    void listagemMissoes_comFiltroStatus() {
        repositoryMissao.save(new Missao(organizacao, "M1", NivelPerigo.BAIXO, StatusMissao.PLANEJADA));
        repositoryMissao.save(new Missao(organizacao, "M2", NivelPerigo.ALTO, StatusMissao.CONCLUIDA));

        var filtro = new FilterConsultaMissao(StatusMissao.PLANEJADA, null, null, null);
        var pagina = missaoService.consultar(filtro, 0, 10, "titulo", false);
        assertEquals(1, pagina.total());
        assertEquals("M1", pagina.content().getFirst().getTitulo());
    }

    @Test
    @DisplayName("Detalhamento de missão: sem participantes — entidade consistente e lista vazia no DTO")
    void detalheMissao_semParticipantes() {
        Missao m = repositoryMissao.save(new Missao(organizacao, "Missão vazia", NivelPerigo.BAIXO, StatusMissao.PLANEJADA));

        Missao carregada = missaoService.buscarDetalhado(m.getId());
        assertTrue(carregada.getParticipacoes().isEmpty());

        var dto = mapperMissao.toDetalhe(carregada);
        assertTrue(dto.participantes().isEmpty());
        assertEquals("Missão vazia", dto.titulo());
    }

    @Test
    @DisplayName("Detalhamento de missão: com participante — papel, recompensa e destaque no retorno")
    void detalheMissao_comParticipante() {
        Aventureiro a = repositoryAventureiro.save(newAventureiro("Participante Um", Classe.MAGO, 5));
        Missao m = repositoryMissao.save(new Missao(organizacao, "Com equipe", NivelPerigo.MEDIO, StatusMissao.EM_ANDAMENTO));
        repositoryParticipacaoMissao.save(new ParticipacaoMissao(m, a, PapelMissao.SUPORTE, 75, false));

        entityManager.flush();
        entityManager.clear();

        Missao carregada = missaoService.buscarDetalhado(m.getId());
        var dto = mapperMissao.toDetalhe(carregada);
        assertEquals(1, dto.participantes().size());
        var part = dto.participantes().getFirst();
        assertEquals(PapelMissao.SUPORTE, part.papel());
        assertEquals(75, part.recompensaOuro());
        assertEquals(false, part.destaque());
    }
}

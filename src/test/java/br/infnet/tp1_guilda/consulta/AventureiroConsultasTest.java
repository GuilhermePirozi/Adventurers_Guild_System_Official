package br.infnet.tp1_guilda.consulta;

import br.infnet.tp1_guilda.domain.aventura.Aventureiro;
import br.infnet.tp1_guilda.domain.aventura.Companheiro;
import br.infnet.tp1_guilda.domain.aventura.Missao;
import br.infnet.tp1_guilda.domain.aventura.ParticipacaoMissao;
import br.infnet.tp1_guilda.domain.aventura.enums.NivelPerigo;
import br.infnet.tp1_guilda.domain.aventura.enums.PapelMissao;
import br.infnet.tp1_guilda.domain.aventura.enums.StatusMissao;
import br.infnet.tp1_guilda.dto.aventureiro.FilterRequestAventureiro;
import br.infnet.tp1_guilda.enums.Classe;
import br.infnet.tp1_guilda.enums.Especie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AventureiroConsultasTest extends ConsultasTestBase {

    @Test
    @DisplayName("Listagem de aventureiros: filtros classe, ativo e nível mínimo reduzem o resultado")
    void listagemAventureiros_comFiltros() {
        Aventureiro mago = new Aventureiro(organizacao, usuario, "Gandalf", Classe.MAGO, 10);
        mago.setAtivo(true);
        Aventureiro guerreiroBaixo = new Aventureiro(organizacao, usuario, "Gimli", Classe.GUERREIRO, 3);
        guerreiroBaixo.setAtivo(true);
        Aventureiro guerreiroAlto = new Aventureiro(organizacao, usuario, "Boromir", Classe.GUERREIRO, 8);
        guerreiroAlto.setAtivo(false);
        repositoryAventureiro.save(mago);
        repositoryAventureiro.save(guerreiroBaixo);
        repositoryAventureiro.save(guerreiroAlto);

        var filtro = new FilterRequestAventureiro(Classe.GUERREIRO, true, 5);
        var pagina = aventureiroService.listar(filtro, 0, 10, "nome", false);
        assertEquals(0, pagina.total());

        var filtro2 = new FilterRequestAventureiro(Classe.GUERREIRO, true, 1);
        var pagina2 = aventureiroService.listar(filtro2, 0, 10, "nome", false);
        assertEquals(1, pagina2.total());
        assertEquals("Gimli", pagina2.content().getFirst().getNome());
    }

    @Test
    @DisplayName("Busca por nome: correspondência parcial (LIKE), paginada via serviço")
    void buscaAventureiros_porTrechoDoNome() {
        repositoryAventureiro.save(newAventureiro("Legolas Greenleaf", Classe.ARQUEIRO, 7));
        repositoryAventureiro.save(newAventureiro("Aragorn Elessar", Classe.GUERREIRO, 9));

        var resultado = aventureiroService.buscarPorNomeTrecho("rag", 0, 10, "nome", false);
        assertEquals(1, resultado.total());
        assertTrue(resultado.content().getFirst().getNome().toLowerCase().contains("arag"));
    }

    @Test
    @DisplayName("Perfil completo: sem companheiro e sem missões — totais zerados e campos opcionais nulos")
    void perfilAventureiro_semCompanheiroSemParticipacoes() {
        Aventureiro a = repositoryAventureiro.save(newAventureiro("Solo", Classe.LADINO, 4));

        var perfil = aventureiroService.buscarPerfilCompleto(a.getId());
        assertEquals(a.getId(), perfil.id());
        assertEquals("Solo", perfil.nome());
        assertNull(perfil.companheiro());
        assertEquals(0L, perfil.totalParticipacoesMissao());
        assertNull(perfil.ultimaParticipacao());
    }

    @Test
    @DisplayName("Perfil completo: com companheiro e participação — contagem e última missão coerentes")
    void perfilAventureiro_comCompanheiroEParticipacao() {
        Aventureiro a = newAventureiro("Líder", Classe.CLERIGO, 6);
        Companheiro c = new Companheiro("Bixo", Especie.CORUJA, 80);
        c.setAventureiro(a);
        a.definirCompanheiro(c);
        a = repositoryAventureiro.save(a);

        Missao m = repositoryMissao.save(new Missao(organizacao, "Missão Alpha", NivelPerigo.MEDIO, StatusMissao.EM_ANDAMENTO));
        repositoryParticipacaoMissao.save(new ParticipacaoMissao(m, a, PapelMissao.LIDER, 120, true));

        var perfil = aventureiroService.buscarPerfilCompleto(a.getId());
        assertNotNull(perfil.companheiro());
        assertEquals("Bixo", perfil.companheiro().nome());
        assertEquals(1L, perfil.totalParticipacoesMissao());
        assertNotNull(perfil.ultimaParticipacao());
        assertEquals(m.getId(), perfil.ultimaParticipacao().missaoId());
    }
}

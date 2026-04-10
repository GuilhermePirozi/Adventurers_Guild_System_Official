package br.infnet.tp1_guilda.consulta;

import br.infnet.tp1_guilda.config.TestCacheConfig;
import br.infnet.tp1_guilda.domain.audit.Organization;
import br.infnet.tp1_guilda.domain.audit.User;
import br.infnet.tp1_guilda.domain.audit.enums.UserStatus;
import br.infnet.tp1_guilda.domain.aventura.Aventureiro;
import br.infnet.tp1_guilda.mapper.MapperAventureiro;
import br.infnet.tp1_guilda.mapper.MapperCompanheiro;
import br.infnet.tp1_guilda.mapper.MapperMissao;
import br.infnet.tp1_guilda.mapper.MapperRelatorio;
import br.infnet.tp1_guilda.repository.audit.RepositoryOrganization;
import br.infnet.tp1_guilda.repository.audit.RepositoryUser;
import br.infnet.tp1_guilda.repository.aventura.RepositoryAventureiro;
import br.infnet.tp1_guilda.repository.aventura.RepositoryMissao;
import br.infnet.tp1_guilda.repository.aventura.RepositoryParticipacaoMissao;
import br.infnet.tp1_guilda.enums.Classe;
import br.infnet.tp1_guilda.service.AventureiroService;
import br.infnet.tp1_guilda.service.MissaoService;
import br.infnet.tp1_guilda.service.RelatorioService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManager;

@DataJpaTest
@ActiveProfiles("test")
@Import({
        TestCacheConfig.class,
        MapperAventureiro.class,
        MapperCompanheiro.class,
        MapperMissao.class,
        MapperRelatorio.class,
        AventureiroService.class,
        MissaoService.class,
        RelatorioService.class
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EntityScan(basePackages = {"br.infnet.tp1_guilda.domain.audit", "br.infnet.tp1_guilda.domain.aventura"})
@EnableJpaRepositories(basePackages = {"br.infnet.tp1_guilda.repository.audit", "br.infnet.tp1_guilda.repository.aventura"})
abstract class ConsultasTestBase {

    @Autowired
    protected RepositoryOrganization repositoryOrganization;
    @Autowired
    protected RepositoryUser repositoryUser;
    @Autowired
    protected RepositoryAventureiro repositoryAventureiro;
    @Autowired
    protected RepositoryMissao repositoryMissao;
    @Autowired
    protected RepositoryParticipacaoMissao repositoryParticipacaoMissao;

    @Autowired
    protected AventureiroService aventureiroService;
    @Autowired
    protected MissaoService missaoService;
    @Autowired
    protected RelatorioService relatorioService;
    @Autowired
    protected MapperMissao mapperMissao;

    @Autowired
    protected EntityManager entityManager;

    protected Organization organizacao;
    protected User usuario;

    @BeforeEach
    void prepararOrganizacaoEUsuario() {
        organizacao = repositoryOrganization.save(
                Organization.builder().nome("Org Consulta TP2").ativo(true).build());
        usuario = repositoryUser.save(User.builder()
                .organizacao(organizacao)
                .nome("Usuario Base")
                .email("usuario.base." + System.nanoTime() + "@teste.local")
                .senhaHash("hash")
                .status(UserStatus.ATIVO)
                .build());
    }

    protected final Aventureiro newAventureiro(String nome, Classe classe, int nivel) {
        return new Aventureiro(organizacao, usuario, nome, classe, nivel);
    }
}

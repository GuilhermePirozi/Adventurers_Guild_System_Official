package br.infnet.tp1_guilda.audit;
import br.infnet.tp1_guilda.domain.audit.Organization;
import br.infnet.tp1_guilda.domain.audit.Permission;
import br.infnet.tp1_guilda.domain.audit.Role;
import br.infnet.tp1_guilda.domain.audit.User;
import br.infnet.tp1_guilda.domain.audit.enums.UserStatus;
import br.infnet.tp1_guilda.repository.audit.RepositoryOrganization;
import br.infnet.tp1_guilda.repository.audit.RepositoryPermission;
import br.infnet.tp1_guilda.repository.audit.RepositoryRole;
import br.infnet.tp1_guilda.repository.audit.RepositoryUser;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import java.util.HashSet;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableJpaRepositories(basePackages = "br.infnet.tp1_guilda.repository.audit")
@EntityScan(basePackages = "br.infnet.tp1_guilda.domain.audit")
class UserJpaTest {

    @Autowired
    RepositoryOrganization repositoryOrganization;
    @Autowired
    RepositoryPermission repositoryPermission;
    @Autowired
    RepositoryRole repositoryRole;
    @Autowired
    RepositoryUser repositoryUser;

    @PersistenceContext
    EntityManager em;

    @Test
    void usuarioRolesOrganizacaoEPermissoesPersistemERecarregam() {
        Organization org = repositoryOrganization.save(
                Organization.builder().nome("Org TP").ativo(true).build());

        Permission ler = repositoryPermission.save(
                Permission.builder().code("LER").descricao("Leitura").build());

        Role role = Role.builder()
                .organizacao(org)
                .nome("AVENTUREIRO")
                .permissions(new HashSet<>(Set.of(ler)))
                .build();
        role = repositoryRole.save(role);

        User salvo = repositoryUser.save(User.builder()
                .organizacao(org)
                .nome("Fulano")
                .email("fulano@org-tp.local")
                .senhaHash("hash")
                .status(UserStatus.ATIVO)
                .roles(new HashSet<>(Set.of(role)))
                .build());

        em.flush();
        em.clear();

        User usuario = repositoryUser.findById(salvo.getId()).orElseThrow();

        assertEquals(1, usuario.getRoles().size());
        Role roleCarregada = usuario.getRoles().iterator().next();
        assertEquals("AVENTUREIRO", roleCarregada.getNome());

        assertEquals(org.getId(), usuario.getOrganizacao().getId());
        assertEquals("Org TP", usuario.getOrganizacao().getNome());

        assertEquals(1, roleCarregada.getPermissions().size());
        assertTrue(roleCarregada.getPermissions().stream().anyMatch(p -> "LER".equals(p.getCode())));
    }
}

package br.infnet.tp1_guilda.repository.audit;

import br.infnet.tp1_guilda.domain.audit.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RepositoryRole extends JpaRepository<Role, Long> {
    List<Role> findByOrganizacaoId(Long organizacaoId);
}
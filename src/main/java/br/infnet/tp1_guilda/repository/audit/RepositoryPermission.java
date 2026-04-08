package br.infnet.tp1_guilda.repository.audit;

import br.infnet.tp1_guilda.domain.audit.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryPermission extends JpaRepository<Permission, Long> {
}
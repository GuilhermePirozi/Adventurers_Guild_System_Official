package br.infnet.tp1_guilda.repository.audit;

import br.infnet.tp1_guilda.domain.audit.AuditEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryAuditEntry extends JpaRepository<AuditEntry, Long> {
}

package br.infnet.tp1_guilda.repository.audit;

import br.infnet.tp1_guilda.domain.audit.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryApiKey extends JpaRepository<ApiKey, Long> {
}

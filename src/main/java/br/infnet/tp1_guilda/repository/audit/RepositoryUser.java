package br.infnet.tp1_guilda.repository.audit;

import br.infnet.tp1_guilda.domain.audit.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepositoryUser extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
package br.infnet.tp1_guilda.repository.aventura;

import br.infnet.tp1_guilda.domain.aventura.Missao;
import br.infnet.tp1_guilda.domain.aventura.enums.NivelPerigo;
import br.infnet.tp1_guilda.domain.aventura.enums.StatusMissao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface RepositoryMissao extends JpaRepository<Missao, Long> {

    @Query("""
        select m
        from Missao m
        where (:status is null or m.status = :status)
          and (:nivelPerigo is null or m.nivelPerigo = :nivelPerigo)
          and m.dataCriacao >= :dataCriacaoDe
          and m.dataCriacao <= :dataCriacaoAte
        """)
    Page<Missao> consultarComFiltro(
            @Param("status") StatusMissao status,
            @Param("nivelPerigo") NivelPerigo nivelPerigo,
            @Param("dataCriacaoDe") OffsetDateTime dataCriacaoDe,
            @Param("dataCriacaoAte") OffsetDateTime dataCriacaoAte,
            Pageable pageable
    );

    @Query("""
            select distinct m from Missao m
            left join fetch m.participacoes p
            left join fetch p.aventureiro
            where m.id = :id
            """)
    Optional<Missao> findDetalhadoById(@Param("id") Long id);
}

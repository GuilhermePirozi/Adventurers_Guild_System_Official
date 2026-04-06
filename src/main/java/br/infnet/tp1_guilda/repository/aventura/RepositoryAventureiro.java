package br.infnet.tp1_guilda.repository.aventura;

import br.infnet.tp1_guilda.domain.aventura.Aventureiro;
import br.infnet.tp1_guilda.enums.Classe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RepositoryAventureiro extends JpaRepository<Aventureiro, Long> {

    @Query("""
            select a
            from Aventureiro a
            where (:classe is null or a.classe = :classe)
              and (:ativo is null or a.ativo = :ativo)
              and (:nivelMinimo is null or a.nivel >= :nivelMinimo)
            """)
    Page<Aventureiro> consultarComFiltro(
            @Param("classe") Classe classe,
            @Param("ativo") Boolean ativo,
            @Param("nivelMinimo") Integer nivelMinimo,
            Pageable pageable
    );

    @Query("""
            select a
            from Aventureiro a
            where lower(a.nome) like lower(concat('%', :trecho, '%'))
            """)
    Page<Aventureiro> buscarPorNomeContendo(
            @Param("trecho") String trecho,
            Pageable pageable
    );
}
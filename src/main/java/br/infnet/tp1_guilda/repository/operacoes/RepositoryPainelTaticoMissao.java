package br.infnet.tp1_guilda.repository.operacoes;

import br.infnet.tp1_guilda.domain.operacoes.PainelTaticoMissao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface RepositoryPainelTaticoMissao extends JpaRepository<PainelTaticoMissao, Long> {

    List<PainelTaticoMissao>findTop10ByUltimaAtualizacaoAfterOrderByIndiceProntidaoDesc(OffsetDateTime data);
}

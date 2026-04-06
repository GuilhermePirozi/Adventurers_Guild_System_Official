package br.infnet.tp1_guilda.dto.operacoes;

import br.infnet.tp1_guilda.domain.aventura.enums.NivelPerigo;
import br.infnet.tp1_guilda.domain.aventura.enums.StatusMissao;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record ResponsePainelTaticoMissao(
        Long missaoId,
        String titulo,
        StatusMissao status,
        NivelPerigo nivelPerigo,
        Long organizacaoId,
        Integer totalParticipantes,
        BigDecimal nivelMedioEquipe,
        BigDecimal totalRecompensa,
        Integer totalMvps,
        Integer participantesComCompanheiro,
        OffsetDateTime ultimaAtualizacao,
        BigDecimal indiceProntidao
) {
}


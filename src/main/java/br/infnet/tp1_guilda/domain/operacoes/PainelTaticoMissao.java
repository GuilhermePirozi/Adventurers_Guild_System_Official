package br.infnet.tp1_guilda.domain.operacoes;

import br.infnet.tp1_guilda.domain.aventura.enums.NivelPerigo;
import br.infnet.tp1_guilda.domain.aventura.enums.StatusMissao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "vw_painel_tatico_missao", schema = "operacoes")
public class PainelTaticoMissao {

    @Id
    @Column(name = "missao_id")
    private Long missaoId;
    @Column(name = "titulo")
    private String titulo;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusMissao status;
    @Column(name = "nivel_perigo")
    @Enumerated(EnumType.STRING)
    private NivelPerigo nivelPerigo;
    @Column(name = "organizacao_id")
    private Long organizacaoId;
    @Column(name = "total_participantes")
    private Integer totalParticipantes;
    @Column(name = "nivel_medio_equipe")
    private BigDecimal nivelMedioEquipe;
    @Column(name = "total_recompensa")
    private BigDecimal totalRecompensa;
    @Column(name = "total_mvps")
    private Integer totalMvps;
    @Column(name = "participantes_com_companheiro")
    private Integer participantesComCompanheiro;
    @Column(name = "ultima_atualizacao")
    private OffsetDateTime ultimaAtualizacao;
    @Column(name = "indice_prontidao")
    private BigDecimal indiceProntidao;
}

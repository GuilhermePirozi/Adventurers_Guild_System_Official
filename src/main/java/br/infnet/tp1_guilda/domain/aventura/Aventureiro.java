package br.infnet.tp1_guilda.domain.aventura;

import br.infnet.tp1_guilda.domain.audit.Organization;
import br.infnet.tp1_guilda.domain.audit.User;
import br.infnet.tp1_guilda.enums.Classe;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Date;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "aventureiros", schema = "aventura")
public class Aventureiro {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "aventureiroId")
    @SequenceGenerator(
            name = "aventureiroId",
            sequenceName = "aventureiro_seq",
            allocationSize = 1,
            schema = "aventura"
    )
    @Column(name = "aventureiro_id")
    private Long id;
    @ManyToOne(optional = false)
    @JoinColumn(name = "organizacao_id", nullable = false)
    private Organization organizacao;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private User user;
    @NotBlank(message = "Tem que haver um nome")
    @Column(name = "nome", nullable = false, length = 120)
    private String nome;
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Tem que haver uma classe")
    @Column(name = "classe", nullable = false)
    private Classe classe;
    @Min(value = 1, message = "O nível deve ser maior ou igual a 1")
    @Column(name = "nivel", nullable = false)
    private int nivel;
    @NotNull
    @Column(name = "ativo", nullable = false)
    private Boolean ativo;
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private OffsetDateTime dataCriacao;
    @Valid
    @OneToOne(mappedBy = "aventureiro", cascade = CascadeType.ALL, orphanRemoval = true)
    private Companheiro companheiro;
    @Column(name = "data_atualizacao", nullable = false)
    private OffsetDateTime dataAtualizacao;

    @PrePersist
    public void prePersist() {
        OffsetDateTime agora = OffsetDateTime.now();
        if (this.dataCriacao == null) {
            this.dataCriacao = agora;
        }
        this.dataAtualizacao = agora;
    }

    @PreUpdate
    public void preUpdate() {
        this.dataAtualizacao = OffsetDateTime.now();
    }

    public Aventureiro(Organization organizacao, User user, String nome, Classe classe, int nivel) {
        this.organizacao = organizacao;
        this.user = user;
        this.nome = nome;
        this.classe = classe;
        this.nivel = nivel;
        this.ativo = true;
    }

    public void alterarNome(String nome) {
        this.nome = nome;
    }

    public void alterarClasse(Classe classe) {
        this.classe = classe;
    }

    public void alterarNivel(int nivel) {
        this.nivel = nivel;
    }

    public void encerrarVinculo() {
        this.ativo = false;
    }

    public void recrutar() {
        this.ativo = true;
    }

    public void definirCompanheiro(Companheiro companheiro) {
        this.companheiro = companheiro;
    }

    public void removerCompanheiro() {
        this.companheiro = null;
    }

}
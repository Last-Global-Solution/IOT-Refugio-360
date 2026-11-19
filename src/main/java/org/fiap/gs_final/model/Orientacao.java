package org.fiap.gs_final.model;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Orientação gerada para uma ocorrência, pela IA ou pela alternativa local.
 * Fica gravada para o cidadão consultar depois e para o gestor ver o que foi
 * recomendado a cada pessoa.
 */
@Entity
@Table(name = "tb_orientacao")
public class Orientacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ocorrencia_id", nullable = false)
    private Ocorrencia ocorrencia;

    @Column(nullable = false, length = 2000)
    private String mensagem;

    private String perfil;

    @Column(nullable = false)
    private boolean geradoPorIA;

    @Column(nullable = false)
    private Instant criadaEm;

    /** Locais seguros que a orientação levou em conta. */
    @ManyToMany
    @JoinTable(name = "tb_orientacao_local",
            joinColumns = @JoinColumn(name = "orientacao_id"),
            inverseJoinColumns = @JoinColumn(name = "local_id"))
    private List<LocalSeguro> locaisConsiderados = new ArrayList<>();

    protected Orientacao() {}

    public Orientacao(Ocorrencia ocorrencia, String mensagem, String perfil, boolean geradoPorIA,
                      List<LocalSeguro> locaisConsiderados) {
        this.ocorrencia = ocorrencia;
        this.mensagem = mensagem;
        this.perfil = perfil;
        this.geradoPorIA = geradoPorIA;
        this.criadaEm = Instant.now();
        this.locaisConsiderados = new ArrayList<>(locaisConsiderados);
    }

    public Long getId() { return id; }
    public Ocorrencia getOcorrencia() { return ocorrencia; }
    public String getMensagem() { return mensagem; }
    public String getPerfil() { return perfil; }
    public boolean isGeradoPorIA() { return geradoPorIA; }
    public Instant getCriadaEm() { return criadaEm; }
    public List<LocalSeguro> getLocaisConsiderados() { return locaisConsiderados; }
}

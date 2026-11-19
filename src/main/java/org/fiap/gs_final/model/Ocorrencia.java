package org.fiap.gs_final.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_ocorrencia")
public class Ocorrencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "A descrição do relato é obrigatória")
    private String descricao;

    @NotBlank(message = "O nível de urgência é obrigatório (ex: BAIXA, MEDIA, CRITICA)")
    private String urgencia;

    private String status = StatusOcorrencia.PENDENTE.name();

    private Double latitude;
    private Double longitude;

    /** Público para quem a orientação deve ser escrita (idoso, pcd, familia). Opcional. */
    private String perfil;

    /** Quem registrou o relato. Ocorrências antigas, anteriores a este vínculo, ficam sem autor. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autor_id")
    private Usuario autor;

    /** Orientações geradas para esta ocorrência; são removidas junto com ela. */
    @OneToMany(mappedBy = "ocorrencia", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OrderBy("criadaEm DESC")
    private List<Orientacao> orientacoes = new ArrayList<>();

    // Construtores
    public Ocorrencia() {}

    public Ocorrencia(String descricao, String urgencia, Double latitude, Double longitude) {
        this.descricao = descricao;
        this.urgencia = urgencia;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Ocorrencia(String descricao, String urgencia, Double latitude, Double longitude,
                      String perfil, Usuario autor) {
        this(descricao, urgencia, latitude, longitude);
        this.perfil = perfil;
        this.autor = autor;
    }

    /**
     * Único ponto que muda o status. Devolve false quando a transição não é permitida,
     * para o serviço responder com 409.
     */
    public boolean moverPara(StatusOcorrencia destino) {
        StatusOcorrencia atual = StatusOcorrencia.deTexto(status);
        if (atual == null) atual = StatusOcorrencia.PENDENTE;
        if (!atual.podeIrPara(destino)) return false;
        this.status = destino.name();
        return true;
    }

    /** Indica se o usuário informado é o autor do relato. */
    public boolean pertenceA(Usuario usuario) {
        return autor != null && usuario != null && autor.getId().equals(usuario.getId());
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getUrgencia() { return urgencia; }
    public void setUrgencia(String urgencia) { this.urgencia = urgencia; }

    public String getStatus() { return status; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getPerfil() { return perfil; }
    public void setPerfil(String perfil) { this.perfil = perfil; }

    public Usuario getAutor() { return autor; }

    public List<Orientacao> getOrientacoes() { return orientacoes; }
}

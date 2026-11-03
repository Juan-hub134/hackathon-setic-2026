package br.com.hackathon.setic.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "parecer_prr")
public class ParecerPrr {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_parecer")
    private Long idParecer;

    @ManyToOne
    @JoinColumn(name = "id_resposta", nullable = false)
    private PlanoResposta planoResposta;

    @ManyToOne
    @JoinColumn(name = "id_usuario_avaliador", nullable = false)
    private Usuario usuarioAvaliador;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_resultado", nullable = false)
    private StatusPrr statusResultado;

    @Column(name = "parecer_texto", nullable = false, columnDefinition = "TEXT")
    private String parecerTexto;

    @Column(name = "avaliado_em")
    private LocalDateTime avaliadoEm;

    public ParecerPrr() {
    }

    public Long getIdParecer() {
        return idParecer;
    }

    public void setIdParecer(Long idParecer) {
        this.idParecer = idParecer;
    }

    public PlanoResposta getPlanoResposta() {
        return planoResposta;
    }

    public void setPlanoResposta(PlanoResposta planoResposta) {
        this.planoResposta = planoResposta;
    }

    public Usuario getUsuarioAvaliador() {
        return usuarioAvaliador;
    }

    public void setUsuarioAvaliador(Usuario usuarioAvaliador) {
        this.usuarioAvaliador = usuarioAvaliador;
    }

    public StatusPrr getStatusResultado() {
        return statusResultado;
    }

    public void setStatusResultado(StatusPrr statusResultado) {
        this.statusResultado = statusResultado;
    }

    public String getParecerTexto() {
        return parecerTexto;
    }

    public void setParecerTexto(String parecerTexto) {
        this.parecerTexto = parecerTexto;
    }

    public LocalDateTime getAvaliadoEm() {
        return avaliadoEm;
    }

    public void setAvaliadoEm(LocalDateTime avaliadoEm) {
        this.avaliadoEm = avaliadoEm;
    }
}

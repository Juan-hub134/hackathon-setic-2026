package br.com.hackathon.setic.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "contexto_etapa")
public class ContextoEtapa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contexto_etapa")
    private Long idContextoEtapa;

    @ManyToOne
    @JoinColumn(name = "id_contexto")
    private Contexto contexto;

    @Column(name = "etapa", nullable = false, columnDefinition = "TEXT")
    private String etapa;

    @Column(name = "prazo", nullable = false)
    private LocalDate prazo;

    @Column(name = "concluido_em")
    private LocalDate concluidoEm;

    @Column(name = "status")
    private Boolean status = false;

    public ContextoEtapa() {
    }

    public Long getIdContextoEtapa() {
        return idContextoEtapa;
    }

    public void setIdContextoEtapa(Long idContextoEtapa) {
        this.idContextoEtapa = idContextoEtapa;
    }

    public Contexto getContexto() {
        return contexto;
    }

    public void setContexto(Contexto contexto) {
        this.contexto = contexto;
    }

    public String getEtapa() {
        return etapa;
    }

    public void setEtapa(String etapa) {
        this.etapa = etapa;
    }

    public LocalDate getPrazo() {
        return prazo;
    }

    public void setPrazo(LocalDate prazo) {
        this.prazo = prazo;
    }

    public LocalDate getConcluidoEm() {
        return concluidoEm;
    }

    public void setConcluidoEm(LocalDate concluidoEm) {
        this.concluidoEm = concluidoEm;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}

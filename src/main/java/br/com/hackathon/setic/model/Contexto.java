package br.com.hackathon.setic.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contexto")
public class Contexto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_contexto")
    private Long idContexto;

    @ManyToOne
    @JoinColumn(name = "id_risco")
    private Risco risco;

    @Column(name = "escopo", nullable = false, columnDefinition = "TEXT")
    private String escopo;

    @Column(name = "criado_em")
    private LocalDateTime criadoEm;

    public Contexto() {
    }

    public Long getIdContexto() {
        return idContexto;
    }

    public void setIdContexto(Long idContexto) {
        this.idContexto = idContexto;
    }

    public Risco getRisco() {
        return risco;
    }

    public void setRisco(Risco risco) {
        this.risco = risco;
    }

    public String getEscopo() {
        return escopo;
    }

    public void setEscopo(String escopo) {
        this.escopo = escopo;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }
}

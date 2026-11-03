package br.com.hackathon.setic.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "solicitacao_csgr")
public class SolicitacaoCsgr {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_solicitacao")
    private Long idSolicitacao;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_resposta", nullable = false)
    private PlanoResposta planoResposta;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario_solicitante", nullable = false)
    private Usuario usuarioSolicitante;

    @ManyToOne
    @JoinColumn(name = "id_usuario_avaliador")
    private Usuario usuarioAvaliador;

    @Column(name = "motivo", nullable = false, columnDefinition = "TEXT")
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private StatusSolicitacaoCsgr status = StatusSolicitacaoCsgr.PENDENTE;

    @Column(name = "solicitado_em", nullable = false)
    private LocalDateTime solicitadoEm;

    @Column(name = "atendido_em")
    private LocalDateTime atendidoEm;

    public Long getIdSolicitacao() {
        return idSolicitacao;
    }

    public void setIdSolicitacao(Long idSolicitacao) {
        this.idSolicitacao = idSolicitacao;
    }

    public PlanoResposta getPlanoResposta() {
        return planoResposta;
    }

    public void setPlanoResposta(PlanoResposta planoResposta) {
        this.planoResposta = planoResposta;
    }

    public Usuario getUsuarioSolicitante() {
        return usuarioSolicitante;
    }

    public void setUsuarioSolicitante(Usuario usuarioSolicitante) {
        this.usuarioSolicitante = usuarioSolicitante;
    }

    public Usuario getUsuarioAvaliador() {
        return usuarioAvaliador;
    }

    public void setUsuarioAvaliador(Usuario usuarioAvaliador) {
        this.usuarioAvaliador = usuarioAvaliador;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public StatusSolicitacaoCsgr getStatus() {
        return status;
    }

    public void setStatus(StatusSolicitacaoCsgr status) {
        this.status = status;
    }

    public LocalDateTime getSolicitadoEm() {
        return solicitadoEm;
    }

    public void setSolicitadoEm(LocalDateTime solicitadoEm) {
        this.solicitadoEm = solicitadoEm;
    }

    public LocalDateTime getAtendidoEm() {
        return atendidoEm;
    }

    public void setAtendidoEm(LocalDateTime atendidoEm) {
        this.atendidoEm = atendidoEm;
    }
}

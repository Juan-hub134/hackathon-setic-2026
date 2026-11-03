package br.com.hackathon.setic.model;

import jakarta.persistence.*;

@Entity
@Table(name = "plano_resposta")
public class PlanoResposta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_resposta")
    private Long idResposta;

    @ManyToOne
    @JoinColumn(name = "id_contexto")
    private Contexto contexto;

    @ManyToOne
    @JoinColumn(name = "id_tratamento")
    private TratamentoRiscos tratamento;

    @ManyToOne
    @JoinColumn(name = "id_parametro")
    private Parametros parametro;

    @Column(name = "nome_responsaveis", nullable = false, columnDefinition = "TEXT")
    private String nomeResponsaveis;

    @Column(name = "contr_existentes", nullable = false, columnDefinition = "TEXT")
    private String controlesExistentes;

    @Column(name = "acao_proposta", nullable = false, columnDefinition = "TEXT")
    private String acaoProposta;

    @Column(name = "metricas", nullable = false, columnDefinition = "TEXT")
    private String metricas;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusPrr status = StatusPrr.PENDENTE;

    public PlanoResposta() {
    }

    public Long getIdResposta() {
        return idResposta;
    }

    public void setIdResposta(Long idResposta) {
        this.idResposta = idResposta;
    }

    public Contexto getContexto() {
        return contexto;
    }

    public void setContexto(Contexto contexto) {
        this.contexto = contexto;
    }

    public TratamentoRiscos getTratamento() {
        return tratamento;
    }

    public void setTratamento(TratamentoRiscos tratamento) {
        this.tratamento = tratamento;
    }

    public Parametros getParametro() {
        return parametro;
    }

    public void setParametro(Parametros parametro) {
        this.parametro = parametro;
    }

    public String getNomeResponsaveis() {
        return nomeResponsaveis;
    }

    public void setNomeResponsaveis(String nomeResponsaveis) {
        this.nomeResponsaveis = nomeResponsaveis;
    }

    public String getControlesExistentes() {
        return controlesExistentes;
    }

    public void setControlesExistentes(String controlesExistentes) {
        this.controlesExistentes = controlesExistentes;
    }

    public String getAcaoProposta() {
        return acaoProposta;
    }

    public void setAcaoProposta(String acaoProposta) {
        this.acaoProposta = acaoProposta;
    }

    public String getMetricas() {
        return metricas;
    }

    public void setMetricas(String metricas) {
        this.metricas = metricas;
    }

    public StatusPrr getStatus() {
        return status;
    }

    public void setStatus(StatusPrr status) {
        this.status = status;
    }
}

package br.com.hackathon.setic.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "risco")
public class Risco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idRisco;
    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;
    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;
    private String causa;
    private String evento;
    private String impactoTxt;
    private String objetivo;
    private String descricao;
    @ManyToOne
    @JoinColumn(name = "id_probabilidade")
    private EscalaProbabilidade escalaProbabilidade;
    @ManyToOne
    @JoinColumn(name = "id_impacto")
    private EscalaImpacto escalaImpacto;
    private int nivel;
    private int horizonteMeses;
    private String criadoPor;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    public long getIdRisco() {
        return idRisco;
    }

    public void setIdRisco(long idRisco) {
        this.idRisco = idRisco;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getCausa() {
        return causa;
    }

    public void setCausa(String causa) {
        this.causa = causa;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public String getImpactoTxt() {
        return impactoTxt;
    }

    public void setImpactoTxt(String impactoTxt) {
        this.impactoTxt = impactoTxt;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public EscalaProbabilidade getEscalaProbabilidade() {
        return escalaProbabilidade;
    }

    public void setEscalaProbabilidade(EscalaProbabilidade escalaProbabilidade) {
        this.escalaProbabilidade = escalaProbabilidade;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public int getHorizonteMeses() {
        return horizonteMeses;
    }

    public void setHorizonteMeses(int horizonteMeses) {
        this.horizonteMeses = horizonteMeses;
    }

    public String getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(String criadoPor) {
        this.criadoPor = criadoPor;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }
}

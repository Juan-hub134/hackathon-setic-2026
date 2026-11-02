package br.com.hackathon.setic.model;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico")
public class Historico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name="id_usuario")
    private Usuario usuario;
    @Column(name = "id_registro")
    private  long idRegistro;
    private String tabela;
    private String operacao;
    private LocalDateTime alteradoEm;
    private String camposAlterados;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition="jsonb")
    private JsonNode antes;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition="jsonb")
    private JsonNode depois;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public long getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(long idRegistro) {
        this.idRegistro = idRegistro;
    }

    public String getTabela() {
        return tabela;
    }

    public void setTabela(String tabela) {
        this.tabela = tabela;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public LocalDateTime getAlteradoEm() {
        return alteradoEm;
    }

    public void setAlteradoEm(LocalDateTime alteradoEm) {
        this.alteradoEm = alteradoEm;
    }

    public String getCamposAlterados() {
        return camposAlterados;
    }

    public void setCamposAlterados(String camposAlterados) {
        this.camposAlterados = camposAlterados;
    }

    public JsonNode getAntes() {
        return antes;
    }

    public void setAntes(JsonNode antes) {
        this.antes = antes;
    }

    public JsonNode getDepois() {
        return depois;
    }

    public void setDepois(JsonNode depois) {
        this.depois = depois;
    }
}
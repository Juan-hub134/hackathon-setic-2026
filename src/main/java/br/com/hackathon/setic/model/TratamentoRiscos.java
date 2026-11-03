package br.com.hackathon.setic.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tratamento_riscos")
public class TratamentoRiscos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tratamento")
    private Long idTratamento;

    @Column(name = "rotulo", nullable = false, columnDefinition = "TEXT")
    private String rotulo;

    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String descricao;

    public TratamentoRiscos() {
    }

    public Long getIdTratamento() {
        return idTratamento;
    }

    public void setIdTratamento(Long idTratamento) {
        this.idTratamento = idTratamento;
    }

    public String getRotulo() {
        return rotulo;
    }

    public void setRotulo(String rotulo) {
        this.rotulo = rotulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}

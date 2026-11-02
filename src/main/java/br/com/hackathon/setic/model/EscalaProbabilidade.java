package br.com.hackathon.setic.model;

import jakarta.persistence.*;

@Entity
@Table(name = "escala_probabilidade")
public class EscalaProbabilidade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long nota;// nota é dois em um, nota é id e nota ao mesmo tempo
    private String rotulo;
    private String descricao;

    public long getNota() {
        return nota;
    }

    public void setNota(long nota) {
        this.nota = nota;
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

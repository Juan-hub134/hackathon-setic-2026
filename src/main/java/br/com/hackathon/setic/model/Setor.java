package br.com.hackathon.setic.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Setor {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long idSetor;
    private String unidade;
    private String sigla;
    public Setor() {}

    public long getIdSetor() {
        return idSetor;
    }

    public void setIdSetor(long idSetor) {
        this.idSetor = idSetor;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }
}

package br.com.hackathon.setic.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cargo")
public class Cargo {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long idCargo;
    private String cargo;
    @Column(columnDefinition = "TEXT")
    private String descricao;

    public Cargo() {}

    public long getIdCargo() {
        return idCargo;
    }

    public void setIdCargo(long idCargo) {
        this.idCargo = idCargo;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}

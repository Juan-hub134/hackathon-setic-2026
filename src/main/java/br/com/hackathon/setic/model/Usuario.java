package br.com.hackathon.setic.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuario")
public class Usuario {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long idUsuario;
    private String nome;
    private String email;
    private String senha;
    @ManyToOne
    @JoinColumn(name = "id_setor")
    private Setor idSetor;
    @ManyToOne
    @JoinColumn(name = "id_cargo")
    private Cargo idCargo;
    private boolean ativo;
    public Usuario() {}

    public long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Setor getIdSetor() {
        return idSetor;
    }

    public void setIdSetor(Setor idSetor) {
        this.idSetor = idSetor;
    }

    public Cargo getIdCargo() {
        return idCargo;
    }

    public void setIdCargo(Cargo idCargo) {
        this.idCargo = idCargo;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }
}

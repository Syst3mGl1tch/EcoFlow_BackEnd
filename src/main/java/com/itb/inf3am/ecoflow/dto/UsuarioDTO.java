package com.itb.inf3am.ecoflow.dto;

import java.time.LocalDateTime;

public class UsuarioDTO {

    private Integer id;
    private String nome;
    private String username;
    private String nivelAcesso;
    private Boolean temFoto;
    private String statusUsuario;
    private LocalDateTime dataCadastro;
    private LocalDateTime dataAtualizacao;

    public UsuarioDTO() {
    }

    public UsuarioDTO(Integer id, String nome, String username, String nivelAcesso, Boolean temFoto,
                      LocalDateTime dataCadastro, LocalDateTime dataAtualizacao, String statusUsuario) {
        this.id = id;
        this.nome = nome;
        this.username = username;
        this.nivelAcesso = nivelAcesso;
        this.temFoto = temFoto;
        this.dataCadastro = dataCadastro;
        this.dataAtualizacao = dataAtualizacao;
        this.statusUsuario = statusUsuario;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNivelAcesso() {
        return nivelAcesso;
    }

    public void setNivelAcesso(String nivelAcesso) {
        this.nivelAcesso = nivelAcesso;
    }

    public Boolean getTemFoto() {
        return temFoto;
    }

    public void setTemFoto(Boolean temFoto) {
        this.temFoto = temFoto;
    }

    public String getStatusUsuario() {
        return statusUsuario;
    }

    public void setStatusUsuario(String statusUsuario) {
        this.statusUsuario = statusUsuario;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}

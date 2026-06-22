package com.itb.inf3am.ecoflow.dto;

import java.time.LocalDateTime;

public class ProdutoDTO {
    private Integer id;
    private String nome;
    private String descricao;
    private LocalDateTime dataCadastro;
    private String telefone;
    private String email;
    private Integer usuarioId;
    private UsuarioDTO usuario;
    private Integer categoriaId;
    private CategoriaDTO categoria;
    private Boolean temFoto;
    private String statusProduto;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public UsuarioDTO getUsuario() { return usuario; }
    public void setUsuario(UsuarioDTO usuario) { this.usuario = usuario; }
    public Integer getCategoriaId() { return categoriaId; }
    public void setCategoriaId(Integer categoriaId) { this.categoriaId = categoriaId; }
    public CategoriaDTO getCategoria() { return categoria; }
    public void setCategoria(CategoriaDTO categoria) { this.categoria = categoria; }
    public Boolean getTemFoto() { return temFoto; }
    public void setTemFoto(Boolean temFoto) { this.temFoto = temFoto; }
    public String getStatusProduto() { return statusProduto; }
    public void setStatusProduto(String statusProduto) { this.statusProduto = statusProduto; }
}

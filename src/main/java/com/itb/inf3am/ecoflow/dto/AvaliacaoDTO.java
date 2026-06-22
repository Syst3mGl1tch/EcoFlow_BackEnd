package com.itb.inf3am.ecoflow.dto;

import java.time.LocalDateTime;

public class AvaliacaoDTO {
    private Integer id;
    private Integer produtoId;
    private ProdutoDTO produto;
    private Integer usuarioId;
    private UsuarioDTO usuario;
    private LocalDateTime dataCadastro;
    private String comentario;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getProdutoId() { return produtoId; }
    public void setProdutoId(Integer produtoId) { this.produtoId = produtoId; }
    public ProdutoDTO getProduto() { return produto; }
    public void setProduto(ProdutoDTO produto) { this.produto = produto; }
    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }
    public UsuarioDTO getUsuario() { return usuario; }
    public void setUsuario(UsuarioDTO usuario) { this.usuario = usuario; }
    public LocalDateTime getDataCadastro() { return dataCadastro; }
    public void setDataCadastro(LocalDateTime dataCadastro) { this.dataCadastro = dataCadastro; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}

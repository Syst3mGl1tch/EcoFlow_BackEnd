package com.itb.inf3am.ecoflow.repository;

import com.itb.inf3am.ecoflow.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    List<Produto> findByCategoria_Id(Integer categoriaId);
    List<Produto> findByUsuario_Id(Integer usuarioId);
    List<Produto> findByCategoria_IdAndUsuario_Id(Integer categoriaId, Integer usuarioId);
    List<Produto> findByStatusProduto(String statusProduto);
    List<Produto> findByCategoria_IdAndStatusProduto(Integer categoriaId, String statusProduto);
    List<Produto> findByUsuario_IdAndStatusProduto(Integer usuarioId, String statusProduto);
    List<Produto> findByCategoria_IdAndUsuario_IdAndStatusProduto(Integer categoriaId, Integer usuarioId, String statusProduto);
    long countByCategoria_Id(Integer categoriaId);
}

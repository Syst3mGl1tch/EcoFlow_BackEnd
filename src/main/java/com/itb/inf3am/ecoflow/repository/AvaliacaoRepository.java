package com.itb.inf3am.ecoflow.repository;

import com.itb.inf3am.ecoflow.entity.Avaliacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Integer> {
    List<Avaliacao> findByProduto_Id(Integer produtoId);
    List<Avaliacao> findByUsuario_Id(Integer usuarioId);
}

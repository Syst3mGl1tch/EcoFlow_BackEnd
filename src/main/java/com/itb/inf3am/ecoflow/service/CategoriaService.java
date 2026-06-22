package com.itb.inf3am.ecoflow.service;

import com.itb.inf3am.ecoflow.dto.CategoriaDTO;
import com.itb.inf3am.ecoflow.entity.Categoria;
import com.itb.inf3am.ecoflow.repository.CategoriaRepository;
import com.itb.inf3am.ecoflow.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class CategoriaService {

    private final CategoriaRepository repository;
    private final ProdutoRepository produtoRepository;

    public CategoriaService(CategoriaRepository repository, ProdutoRepository produtoRepository) {
        this.repository = repository;
        this.produtoRepository = produtoRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoriaDTO> listarTodas() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public CategoriaDTO buscarPorId(Integer id) {
        return toDTO(buscarEntidade(id));
    }

    public CategoriaDTO criar(CategoriaDTO dto) {
        validarNome(dto.getNome());
        String nome = dto.getNome().trim();
        if (repository.existsByNomeIgnoreCase(nome)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Categoria ja cadastrada");
        }
        Categoria categoria = new Categoria();
        categoria.setNome(nome);
        return toDTO(repository.save(categoria));
    }

    public CategoriaDTO atualizar(Integer id, CategoriaDTO dto) {
        Categoria categoria = buscarEntidade(id);
        validarNome(dto.getNome());
        String nome = dto.getNome().trim();
        if (repository.existsByNomeIgnoreCaseAndIdNot(nome, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Categoria ja cadastrada");
        }
        categoria.setNome(nome);
        return toDTO(repository.save(categoria));
    }

    public void deletar(Integer id) {
        buscarEntidade(id);
        if (produtoRepository.countByCategoria_Id(id) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Nao e possivel excluir categoria com produtos vinculados");
        }
        repository.deleteById(id);
    }

    public Categoria buscarEntidade(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria nao encontrada: " + id));
    }

    public CategoriaDTO toDTO(Categoria categoria) {
        return new CategoriaDTO(categoria.getId(), categoria.getNome());
    }

    private void validarNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome da categoria obrigatorio");
        }
    }
}

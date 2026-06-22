package com.itb.inf3am.ecoflow.service;

import com.itb.inf3am.ecoflow.dto.AvaliacaoDTO;
import com.itb.inf3am.ecoflow.entity.Avaliacao;
import com.itb.inf3am.ecoflow.entity.Produto;
import com.itb.inf3am.ecoflow.entity.Usuario;
import com.itb.inf3am.ecoflow.repository.AvaliacaoRepository;
import com.itb.inf3am.ecoflow.repository.ProdutoRepository;
import com.itb.inf3am.ecoflow.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class AvaliacaoService {

    private final AvaliacaoRepository repository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProdutoService produtoService;
    private final UsuarioService usuarioService;

    public AvaliacaoService(AvaliacaoRepository repository, ProdutoRepository produtoRepository,
                            UsuarioRepository usuarioRepository, ProdutoService produtoService,
                            UsuarioService usuarioService) {
        this.repository = repository;
        this.produtoRepository = produtoRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoService = produtoService;
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoDTO> listarTodas() {
        return repository.findAll().stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public AvaliacaoDTO buscarPorId(Integer id) {
        return toDTO(findById(id));
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoDTO> listarPorProduto(Integer produtoId) {
        return repository.findByProduto_Id(produtoId).stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public List<AvaliacaoDTO> listarPorUsuario(Integer usuarioId) {
        return repository.findByUsuario_Id(usuarioId).stream().map(this::toDTO).toList();
    }

    public AvaliacaoDTO criar(AvaliacaoDTO dto) {
        validarComentario(dto.getComentario());
        if (dto.getProdutoId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Produto obrigatorio");
        }
        if (dto.getUsuarioId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario obrigatorio");
        }

        Produto produto = produtoRepository.findById(dto.getProdutoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto nao encontrado"));
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setComentario(dto.getComentario().trim());
        avaliacao.setDataCadastro(LocalDateTime.now());
        avaliacao.setProduto(produto);
        avaliacao.setUsuario(usuario);
        return toDTO(repository.save(avaliacao));
    }

    public AvaliacaoDTO atualizar(Integer id, AvaliacaoDTO dto) {
        validarComentario(dto.getComentario());
        Avaliacao avaliacao = findById(id);
        avaliacao.setComentario(dto.getComentario().trim());
        return toDTO(repository.save(avaliacao));
    }

    public void deletar(Integer id) {
        Avaliacao avaliacao = findById(id);
        repository.delete(avaliacao);
    }

    private Avaliacao findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Avaliacao nao encontrada: " + id));
    }

    private AvaliacaoDTO toDTO(Avaliacao a) {
        AvaliacaoDTO dto = new AvaliacaoDTO();
        dto.setId(a.getId());
        dto.setComentario(a.getComentario());
        dto.setDataCadastro(a.getDataCadastro());
        dto.setProdutoId(a.getProduto().getId());
        dto.setProduto(produtoService.toDTO(a.getProduto()));
        dto.setUsuarioId(a.getUsuario().getId());
        dto.setUsuario(usuarioService.toDTO(a.getUsuario()));
        return dto;
    }

    private void validarComentario(String comentario) {
        if (comentario == null || comentario.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comentario obrigatorio");
        }
    }
}

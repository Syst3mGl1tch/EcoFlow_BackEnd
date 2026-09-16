package com.itb.inf3am.ecoflow.service;

import com.itb.inf3am.ecoflow.dto.CategoriaDTO;
import com.itb.inf3am.ecoflow.dto.ProdutoDTO;
import com.itb.inf3am.ecoflow.entity.Categoria;
import com.itb.inf3am.ecoflow.entity.Produto;
import com.itb.inf3am.ecoflow.entity.Usuario;
import com.itb.inf3am.ecoflow.repository.CategoriaRepository;
import com.itb.inf3am.ecoflow.repository.ProdutoRepository;
import com.itb.inf3am.ecoflow.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ProdutoService {

    private final ProdutoRepository repository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioService usuarioService;

    public ProdutoService(ProdutoRepository repository, UsuarioRepository usuarioRepository,
                          CategoriaRepository categoriaRepository, UsuarioService usuarioService) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public List<ProdutoDTO> listar(Integer categoriaId, Integer usuarioId) {
        List<Produto> produtos;
        if (categoriaId != null && usuarioId != null) {
            produtos = repository.findByCategoria_IdAndUsuario_IdAndStatusProduto(categoriaId, usuarioId, "ATIVO");
        } else if (categoriaId != null) {
            produtos = repository.findByCategoria_IdAndStatusProduto(categoriaId, "ATIVO");
        } else if (usuarioId != null) {
            produtos = repository.findByUsuario_IdAndStatusProduto(usuarioId, "ATIVO");
        } else {
            produtos = repository.findByStatusProduto("ATIVO");
        }
        return produtos.stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public ProdutoDTO buscarPorId(Integer id) {
        return toDTO(findActiveById(id));
    }

    public ProdutoDTO criar(ProdutoDTO dto) {
        validarObrigatorio(dto.getNome(), "Nome do produto obrigatorio");
        validarObrigatorio(dto.getDescricao(), "Descricao obrigatoria");
        if (dto.getUsuarioId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuario obrigatorio");
        }
        if (dto.getCategoriaId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Categoria obrigatoria");
        }

        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado"));
        Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria nao encontrada"));

        Produto produto = new Produto();
        produto.setNome(dto.getNome().trim());
        produto.setDescricao(dto.getDescricao().trim());
        produto.setTelefone(normalizarOpcional(dto.getTelefone()));
        produto.setEmail(normalizarOpcional(dto.getEmail()));
        produto.setStatusProduto(dto.getStatusProduto() != null ? dto.getStatusProduto() : "ATIVO");
        produto.setDataCadastro(LocalDateTime.now());
        produto.setUsuario(usuario);
        produto.setCategoria(categoria);

        return toDTO(repository.save(produto));
    }

    public ProdutoDTO atualizar(Integer id, ProdutoDTO dto) {
        Produto produto = findById(id);

        if (dto.getNome() != null) {
            validarObrigatorio(dto.getNome(), "Nome do produto obrigatorio");
            produto.setNome(dto.getNome().trim());
        }
        if (dto.getDescricao() != null) {
            validarObrigatorio(dto.getDescricao(), "Descricao obrigatoria");
            produto.setDescricao(dto.getDescricao().trim());
        }
        if (dto.getTelefone() != null) {
            produto.setTelefone(normalizarOpcional(dto.getTelefone()));
        }
        if (dto.getEmail() != null) {
            produto.setEmail(normalizarOpcional(dto.getEmail()));
        }
        if (dto.getStatusProduto() != null) {
            produto.setStatusProduto(dto.getStatusProduto());
        }
        if (dto.getCategoriaId() != null) {
            produto.setCategoria(categoriaRepository.findById(dto.getCategoriaId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria nao encontrada")));
        }

        return toDTO(repository.save(produto));
    }

    public void salvarFoto(Integer id, MultipartFile foto) {
        Produto produto = findById(id);
        try {
            produto.setFoto(validarImagem(foto));
            repository.save(produto);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao processar foto");
        }
    }

    @Transactional(readOnly = true)
    public byte[] buscarFoto(Integer id) {
        Produto produto = findById(id);
        if (produto.getFoto() == null || produto.getFoto().length == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Foto nao encontrada");
        }
        return produto.getFoto();
    }

    @Transactional(readOnly = true)
    public MediaType detectarTipoImagem(byte[] foto) {
        if (ehPng(foto)) {
            return MediaType.IMAGE_PNG;
        }
        if (ehJpeg(foto)) {
            return MediaType.IMAGE_JPEG;
        }
        if (ehWebp(foto)) {
            return MediaType.parseMediaType("image/webp");
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Formato de foto nao reconhecido");
    }

    public void deletar(Integer id) {
        Produto produto = findById(id);
        produto.setStatusProduto("INATIVO");
        repository.save(produto);
    }

    public Produto findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto nao encontrado: " + id));
    }

    private Produto findActiveById(Integer id) {
        Produto produto = findById(id);
        if (!"ATIVO".equals(produto.getStatusProduto())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto nao encontrado: " + id);
        }
        return produto;
    }

    public ProdutoDTO toDTO(Produto p) {
        ProdutoDTO dto = new ProdutoDTO();
        dto.setId(p.getId());
        dto.setNome(p.getNome());
        dto.setDescricao(p.getDescricao());
        dto.setDataCadastro(p.getDataCadastro());
        dto.setTelefone(p.getTelefone());
        dto.setEmail(p.getEmail());
        dto.setStatusProduto(p.getStatusProduto());
        dto.setTemFoto(p.getFoto() != null && p.getFoto().length > 0);
        dto.setUsuarioId(p.getUsuario().getId());
        dto.setUsuario(usuarioService.toDTO(p.getUsuario()));
        dto.setCategoriaId(p.getCategoria().getId());
        dto.setCategoria(toCategoriaDTO(p.getCategoria()));
        return dto;
    }

    public CategoriaDTO toCategoriaDTO(Categoria categoria) {
        return new CategoriaDTO(categoria.getId(), categoria.getNome());
    }

    private void validarObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
        }
    }

    private String normalizarOpcional(String valor) {
        return valor == null || valor.trim().isEmpty() ? null : valor.trim();
    }

    private byte[] validarImagem(MultipartFile foto) throws IOException {
        if (foto == null || foto.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Foto obrigatoria");
        }
        if (foto.getSize() > 1024 * 1024) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A foto deve ter no maximo 1 MB");
        }

        String tipo = foto.getContentType();
        if (!MediaType.IMAGE_JPEG_VALUE.equals(tipo) && !MediaType.IMAGE_PNG_VALUE.equals(tipo)
                && !"image/webp".equals(tipo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de imagem invalido");
        }

        byte[] dados = foto.getBytes();
        boolean correspondeAoTipo = (MediaType.IMAGE_JPEG_VALUE.equals(tipo) && ehJpeg(dados))
                || (MediaType.IMAGE_PNG_VALUE.equals(tipo) && ehPng(dados))
                || ("image/webp".equals(tipo) && ehWebp(dados));
        if (!correspondeAoTipo) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Conteudo da imagem invalido");
        }
        return dados;
    }

    private boolean ehJpeg(byte[] foto) {
        return foto.length >= 3 && (foto[0] & 0xFF) == 0xFF && (foto[1] & 0xFF) == 0xD8 && (foto[2] & 0xFF) == 0xFF;
    }

    private boolean ehPng(byte[] foto) {
        return foto.length >= 8 && foto[0] == (byte) 0x89 && foto[1] == 0x50 && foto[2] == 0x4E && foto[3] == 0x47
                && foto[4] == 0x0D && foto[5] == 0x0A && foto[6] == 0x1A && foto[7] == 0x0A;
    }

    private boolean ehWebp(byte[] foto) {
        return foto.length >= 12 && foto[0] == 'R' && foto[1] == 'I' && foto[2] == 'F' && foto[3] == 'F'
                && foto[8] == 'W' && foto[9] == 'E' && foto[10] == 'B' && foto[11] == 'P';
    }
}

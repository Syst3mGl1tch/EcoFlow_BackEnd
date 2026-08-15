package com.itb.inf3am.ecoflow.service;

import com.itb.inf3am.ecoflow.dto.UsuarioDTO;
import com.itb.inf3am.ecoflow.entity.Categoria;
import com.itb.inf3am.ecoflow.entity.Produto;
import com.itb.inf3am.ecoflow.entity.Usuario;
import com.itb.inf3am.ecoflow.repository.CategoriaRepository;
import com.itb.inf3am.ecoflow.repository.ProdutoRepository;
import com.itb.inf3am.ecoflow.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ProdutoServiceTest {

    private final ProdutoRepository repository = mock(ProdutoRepository.class);
    private final UsuarioService usuarioService = mock(UsuarioService.class);
    private final ProdutoService service = new ProdutoService(repository, mock(UsuarioRepository.class),
            mock(CategoriaRepository.class), usuarioService);

    @Test
    void deletarMarcaProdutoComoInativoSemRemover() {
        Produto produto = produto(1, "ATIVO");
        when(repository.findById(1)).thenReturn(Optional.of(produto));

        service.deletar(1);

        assertEquals("INATIVO", produto.getStatusProduto());
        verify(repository).save(produto);
        verify(repository, never()).delete(any(Produto.class));
    }

    @Test
    void deletarProdutoInexistenteRetornaNotFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.deletar(99));

        assertEquals(404, exception.getStatusCode().value());
        verify(repository, never()).save(any());
    }

    @Test
    void listarBuscaSomenteProdutosAtivos() {
        Produto produto = produto(1, "ATIVO");
        when(repository.findByStatusProduto("ATIVO")).thenReturn(List.of(produto));
        when(usuarioService.toDTO(produto.getUsuario())).thenReturn(new UsuarioDTO());

        assertEquals(1, service.listar(null, null).size());
        verify(repository).findByStatusProduto("ATIVO");
        verify(repository, never()).findAll();
    }

    private Produto produto(Integer id, String status) {
        Usuario usuario = new Usuario();
        usuario.setId(10);
        Categoria categoria = new Categoria();
        categoria.setId(20);
        categoria.setNome("Categoria");

        Produto produto = new Produto();
        produto.setId(id);
        produto.setNome("Produto");
        produto.setDescricao("Descricao");
        produto.setUsuario(usuario);
        produto.setCategoria(categoria);
        produto.setStatusProduto(status);
        return produto;
    }
}

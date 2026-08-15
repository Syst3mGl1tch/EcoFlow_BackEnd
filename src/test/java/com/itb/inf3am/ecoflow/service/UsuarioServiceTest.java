package com.itb.inf3am.ecoflow.service;

import com.itb.inf3am.ecoflow.entity.Usuario;
import com.itb.inf3am.ecoflow.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UsuarioServiceTest {

    private final UsuarioRepository repository = mock(UsuarioRepository.class);
    private final UsuarioService service = new UsuarioService(repository, mock(PasswordEncoder.class));

    @Test
    void deletarMarcaUsuarioComoInativoSemRemover() {
        Usuario usuario = usuario(1, "ATIVO");
        when(repository.findById(1)).thenReturn(Optional.of(usuario));

        service.deletar(1);

        assertEquals("INATIVO", usuario.getStatusUsuario());
        verify(repository).save(usuario);
        verify(repository, never()).delete(any(Usuario.class));
    }

    @Test
    void deletarUsuarioInexistenteRetornaNotFound() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> service.deletar(99));

        assertEquals(404, exception.getStatusCode().value());
        verify(repository, never()).save(any());
    }

    @Test
    void listarTodosBuscaSomenteUsuariosAtivos() {
        when(repository.findByStatusUsuario("ATIVO")).thenReturn(List.of(usuario(1, "ATIVO")));

        assertEquals(1, service.listarTodos().size());
        verify(repository).findByStatusUsuario("ATIVO");
        verify(repository, never()).findAll();
    }

    private Usuario usuario(Integer id, String status) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNome("Nome");
        usuario.setUsername("nome@exemplo.com");
        usuario.setStatusUsuario(status);
        return usuario;
    }
}

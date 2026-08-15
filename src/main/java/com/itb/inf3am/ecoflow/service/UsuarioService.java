package com.itb.inf3am.ecoflow.service;

import com.itb.inf3am.ecoflow.dto.CreateUsuarioDTO;
import com.itb.inf3am.ecoflow.dto.UpdateUsuarioDTO;
import com.itb.inf3am.ecoflow.dto.UsuarioDTO;
import com.itb.inf3am.ecoflow.entity.Usuario;
import com.itb.inf3am.ecoflow.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Usuario usuario = repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado"));

        if (!"ATIVO".equals(usuario.getStatusUsuario())) {
            throw new DisabledException("Usuario inativo");
        }

        return User.builder()
                .username(usuario.getUsername())
                .password(usuario.getPassword())
                .roles(usuario.getNivelAcesso() != null ? usuario.getNivelAcesso() : "USER")
                .build();
    }

    @Transactional(readOnly = true)
    public List<UsuarioDTO> listarTodos() {
        return repository.findByStatusUsuario("ATIVO").stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioDTO buscarPorId(Integer id) {
        return toDTO(findActiveById(id));
    }

    public UsuarioDTO criar(CreateUsuarioDTO dto) {
        validarObrigatorio(dto.getNome(), "Nome obrigatorio");
        validarObrigatorio(dto.getUsername(), "Username obrigatorio");
        validarObrigatorio(dto.getPassword(), "Senha obrigatoria");

        if (repository.existsByUsername(dto.getUsername().trim())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail ja cadastrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome().trim());
        usuario.setUsername(dto.getUsername().trim());
        usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
        usuario.setStatusUsuario("ATIVO");
        usuario.setNivelAcesso("USER");
        usuario.setDataCadastro(LocalDateTime.now());
        usuario.setDataAtualizacao(null);

        return toDTO(repository.save(usuario));
    }

    public UsuarioDTO atualizar(Integer id, UpdateUsuarioDTO dto) {
        Usuario usuario = findById(id);

        if (dto.getNome() != null) {
            validarObrigatorio(dto.getNome(), "Nome obrigatorio");
            usuario.setNome(dto.getNome().trim());
        }

        if (dto.getUsername() != null && !dto.getUsername().equalsIgnoreCase(usuario.getUsername())) {
            validarObrigatorio(dto.getUsername(), "Username obrigatorio");
            if (repository.existsByUsername(dto.getUsername().trim())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail ja cadastrado");
            }
            usuario.setUsername(dto.getUsername().trim());
        }

        usuario.setDataAtualizacao(LocalDateTime.now());
        return toDTO(repository.save(usuario));
    }

    public void salvarFoto(Integer id, MultipartFile foto) {
        if (foto == null || foto.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Foto obrigatoria");
        }
        Usuario usuario = findById(id);
        try {
            usuario.setFoto(foto.getBytes());
            usuario.setDataAtualizacao(LocalDateTime.now());
            repository.save(usuario);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao processar foto");
        }
    }

    @Transactional(readOnly = true)
    public byte[] buscarFoto(Integer id) {
        Usuario usuario = findById(id);
        if (usuario.getFoto() == null || usuario.getFoto().length == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Foto nao encontrada");
        }
        return usuario.getFoto();
    }

    @Transactional(readOnly = true)
    public MediaType detectarTipoImagem(byte[] foto) {
        if (foto.length >= 4 && foto[0] == (byte) 0x89 && foto[1] == 0x50 && foto[2] == 0x4E && foto[3] == 0x47) {
            return MediaType.IMAGE_PNG;
        }
        if (foto.length >= 3 && (foto[0] & 0xFF) == 0xFF && (foto[1] & 0xFF) == 0xD8) {
            return MediaType.IMAGE_JPEG;
        }
        if (foto.length >= 12 && foto[0] == 'R' && foto[1] == 'I' && foto[2] == 'F' && foto[3] == 'F') {
            return MediaType.parseMediaType("image/webp");
        }
        return MediaType.IMAGE_JPEG;
    }

    public void deletar(Integer id) {
        Usuario usuario = findById(id);
        usuario.setStatusUsuario("INATIVO");
        usuario.setDataAtualizacao(LocalDateTime.now());
        repository.save(usuario);
    }

    public Usuario findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado: " + id));
    }

    private Usuario findActiveById(Integer id) {
        Usuario usuario = findById(id);
        if (!"ATIVO".equals(usuario.getStatusUsuario())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario nao encontrado: " + id);
        }
        return usuario;
    }

    public UsuarioDTO toDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getUsername(),
                usuario.getNivelAcesso(),
                usuario.getFoto() != null && usuario.getFoto().length > 0,
                usuario.getDataCadastro(),
                usuario.getDataAtualizacao(),
                usuario.getStatusUsuario()
        );
    }

    private void validarObrigatorio(String valor, String mensagem) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, mensagem);
        }
    }
}

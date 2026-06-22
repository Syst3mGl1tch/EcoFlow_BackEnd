package com.itb.inf3am.ecoflow.controller;

import com.itb.inf3am.ecoflow.dto.CreateUsuarioDTO;
import com.itb.inf3am.ecoflow.dto.UpdateUsuarioDTO;
import com.itb.inf3am.ecoflow.dto.UsuarioDTO;
import com.itb.inf3am.ecoflow.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<UsuarioDTO> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioDTO> buscar(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> criar(@RequestBody CreateUsuarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> atualizar(@PathVariable Integer id, @RequestBody UpdateUsuarioDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/foto")
    public ResponseEntity<Void> salvarFoto(@PathVariable Integer id, @RequestParam("foto") MultipartFile foto) {
        service.salvarFoto(id, foto);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(value = "/{id}/foto", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> buscarFoto(@PathVariable Integer id) {
        byte[] foto = service.buscarFoto(id);
        MediaType tipo = service.detectarTipoImagem(foto);
        return ResponseEntity.ok().contentType(tipo).body(foto);
    }
}

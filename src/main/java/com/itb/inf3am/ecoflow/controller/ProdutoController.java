package com.itb.inf3am.ecoflow.controller;

import com.itb.inf3am.ecoflow.dto.ProdutoDTO;
import com.itb.inf3am.ecoflow.service.ProdutoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProdutoDTO> listar(
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) Integer usuarioId) {
        return service.listar(categoriaId, usuarioId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDTO> buscar(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ProdutoDTO> criar(@RequestBody ProdutoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoDTO> atualizar(@PathVariable Integer id, @RequestBody ProdutoDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/foto")
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

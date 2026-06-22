package com.itb.inf3am.ecoflow.controller;

import com.itb.inf3am.ecoflow.dto.AvaliacaoDTO;
import com.itb.inf3am.ecoflow.service.AvaliacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoService service;

    public AvaliacaoController(AvaliacaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<AvaliacaoDTO> listar() {
        return service.listarTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AvaliacaoDTO> buscar(@PathVariable Integer id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/produto/{produtoId}")
    public List<AvaliacaoDTO> listarPorProduto(@PathVariable Integer produtoId) {
        return service.listarPorProduto(produtoId);
    }

    @GetMapping("/usuario/{usuarioId}")
    public List<AvaliacaoDTO> listarPorUsuario(@PathVariable Integer usuarioId) {
        return service.listarPorUsuario(usuarioId);
    }

    @PostMapping
    public ResponseEntity<AvaliacaoDTO> criar(@RequestBody AvaliacaoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AvaliacaoDTO> atualizar(@PathVariable Integer id, @RequestBody AvaliacaoDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Integer id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

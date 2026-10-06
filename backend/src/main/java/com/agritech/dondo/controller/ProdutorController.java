package com.agritech.dondo.controller;

import com.agritech.dondo.dto.ProdutorDTO;
import com.agritech.dondo.service.ProdutorService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtores")
public class ProdutorController {

    private final ProdutorService produtorService;

    public ProdutorController(ProdutorService produtorService) {
        this.produtorService = produtorService;
    }

    @GetMapping
    public ResponseEntity<List<ProdutorDTO>> listarTodos() {
        return ResponseEntity.ok(produtorService.listarTodos());
    }

    @GetMapping("/associacao/{associacaoId}")
    public ResponseEntity<List<ProdutorDTO>> listarPorAssociacao(@PathVariable Long associacaoId) {
        return ResponseEntity.ok(produtorService.listarPorAssociacao(associacaoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutorDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(produtorService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUTOR')")
    public ResponseEntity<ProdutorDTO> criar(@Valid @RequestBody ProdutorDTO dto) {
        return ResponseEntity.ok(produtorService.criar(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUTOR')")
    public ResponseEntity<ProdutorDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ProdutorDTO dto) {
        return ResponseEntity.ok(produtorService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        produtorService.remover(id);
        return ResponseEntity.noContent().build();
    }
}

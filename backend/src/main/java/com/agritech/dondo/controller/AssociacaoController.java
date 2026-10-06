package com.agritech.dondo.controller;

import com.agritech.dondo.dto.AssociacaoDTO;
import com.agritech.dondo.service.AssociacaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/associacoes")
public class AssociacaoController {

    private final AssociacaoService associacaoService;

    public AssociacaoController(AssociacaoService associacaoService) {
        this.associacaoService = associacaoService;
    }

    @GetMapping
    public ResponseEntity<List<AssociacaoDTO>> listarTodas() {
        return ResponseEntity.ok(associacaoService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssociacaoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(associacaoService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AssociacaoDTO> criar(@Valid @RequestBody AssociacaoDTO dto) {
        return ResponseEntity.ok(associacaoService.criar(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AssociacaoDTO> atualizar(@PathVariable Long id, @Valid @RequestBody AssociacaoDTO dto) {
        return ResponseEntity.ok(associacaoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        associacaoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}

package com.agritech.dondo.controller;

import com.agritech.dondo.dto.LoteProducaoDTO;
import com.agritech.dondo.model.EstadoLote;
import com.agritech.dondo.service.LoteProducaoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/lotes")
public class LoteProducaoController {

    private final LoteProducaoService loteProducaoService;

    public LoteProducaoController(LoteProducaoService loteProducaoService) {
        this.loteProducaoService = loteProducaoService;
    }

    /**
     * RF04: Catálogo Agrícola Aberto para compradores com filtros
     */
    @GetMapping("/catalogo")
    public ResponseEntity<List<LoteProducaoDTO>> pesquisarCatalogo(
            @RequestParam(required = false) Long culturaId,
            @RequestParam(required = false) String localidade,
            @RequestParam(required = false) EstadoLote estado,
            @RequestParam(required = false) BigDecimal quantidadeMinima) {
        return ResponseEntity.ok(loteProducaoService.pesquisarCatalogo(culturaId, localidade, estado, quantidadeMinima));
    }

    @GetMapping
    public ResponseEntity<List<LoteProducaoDTO>> listarTodos() {
        return ResponseEntity.ok(loteProducaoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LoteProducaoDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(loteProducaoService.buscarPorId(id));
    }

    @GetMapping("/produtor/{produtorId}")
    public ResponseEntity<List<LoteProducaoDTO>> listarPorProdutor(@PathVariable Long produtorId) {
        return ResponseEntity.ok(loteProducaoService.listarPorProdutor(produtorId));
    }

    @GetMapping("/associacao/{associacaoId}")
    public ResponseEntity<List<LoteProducaoDTO>> listarPorAssociacao(@PathVariable Long associacaoId) {
        return ResponseEntity.ok(loteProducaoService.listarPorAssociacao(associacaoId));
    }

    /**
     * RF03: Registo de lotes de produção (Oferta)
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUTOR')")
    public ResponseEntity<LoteProducaoDTO> criar(@Valid @RequestBody LoteProducaoDTO dto) {
        return ResponseEntity.ok(loteProducaoService.criar(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUTOR')")
    public ResponseEntity<LoteProducaoDTO> atualizar(@PathVariable Long id, @Valid @RequestBody LoteProducaoDTO dto) {
        return ResponseEntity.ok(loteProducaoService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'PRODUTOR')")
    public ResponseEntity<LoteProducaoDTO> atualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoLote novoEstado) {
        return ResponseEntity.ok(loteProducaoService.atualizarEstado(id, novoEstado));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        loteProducaoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}

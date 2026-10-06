package com.agritech.dondo.controller;

import com.agritech.dondo.model.Cultura;
import com.agritech.dondo.service.CulturaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/culturas")
public class CulturaController {

    private final CulturaService culturaService;

    public CulturaController(CulturaService culturaService) {
        this.culturaService = culturaService;
    }

    @GetMapping
    public ResponseEntity<List<Cultura>> listarTodas() {
        return ResponseEntity.ok(culturaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cultura> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(culturaService.buscarPorId(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Cultura> criar(@RequestBody Cultura cultura) {
        return ResponseEntity.ok(culturaService.criar(cultura));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Cultura> atualizar(@PathVariable Long id, @RequestBody Cultura cultura) {
        Cultura existente = culturaService.buscarPorId(id);
        existente.setNome(cultura.getNome());
        existente.setCategoria(cultura.getCategoria());
        existente.setUnidadeMedida(cultura.getUnidadeMedida());
        return ResponseEntity.ok(culturaService.criar(existente));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        culturaService.remover(id);
        return ResponseEntity.noContent().build();
    }
}

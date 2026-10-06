package com.agritech.dondo.controller;

import com.agritech.dondo.dto.SyncBatchDTO;
import com.agritech.dondo.dto.SyncResultDTO;
import com.agritech.dondo.model.SincronizacaoLog;
import com.agritech.dondo.service.SincronizacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sync")
public class SincronizacaoController {

    private final SincronizacaoService sincronizacaoService;

    public SincronizacaoController(SincronizacaoService sincronizacaoService) {
        this.sincronizacaoService = sincronizacaoService;
    }

    /**
     * RF07: Endpoint de sincronizacao em lote para operacoes offline do PWA
     */
    @PostMapping
    public ResponseEntity<SyncResultDTO> sincronizarLote(@RequestBody SyncBatchDTO batch) {
        return ResponseEntity.ok(sincronizacaoService.processarLote(batch));
    }

    /**
     * RF08: Historico de sincronizacoes para o painel de supervisao do ADMIN
     */
    @GetMapping("/historico")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SincronizacaoLog>> listarHistorico() {
        return ResponseEntity.ok(sincronizacaoService.listarHistorico());
    }
}

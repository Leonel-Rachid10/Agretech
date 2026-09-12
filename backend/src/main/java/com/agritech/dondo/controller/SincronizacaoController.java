package com.agritech.dondo.controller;

import com.agritech.dondo.dto.SyncBatchDTO;
import com.agritech.dondo.dto.SyncResultDTO;
import com.agritech.dondo.service.SincronizacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sync")
public class SincronizacaoController {

    private final SincronizacaoService sincronizacaoService;

    public SincronizacaoController(SincronizacaoService sincronizacaoService) {
        this.sincronizacaoService = sincronizacaoService;
    }

    /**
     * RF07 & RF08: Endpoint de sincronização em lote para operações offline do PWA
     */
    @PostMapping
    public ResponseEntity<SyncResultDTO> sincronizarLote(@RequestBody SyncBatchDTO batch) {
        return ResponseEntity.ok(sincronizacaoService.processarLote(batch));
    }
}

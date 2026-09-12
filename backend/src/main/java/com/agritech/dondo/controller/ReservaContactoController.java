package com.agritech.dondo.controller;

import com.agritech.dondo.dto.ReservaContactoDTO;
import com.agritech.dondo.model.EstadoNegocio;
import com.agritech.dondo.service.ReservaContactoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaContactoController {

    private final ReservaContactoService reservaContactoService;

    public ReservaContactoController(ReservaContactoService reservaContactoService) {
        this.reservaContactoService = reservaContactoService;
    }

    /**
     * RF05: Registo de conexão de negócio (Comprador -> Lote)
     */
    @PostMapping
    public ResponseEntity<ReservaContactoDTO> registarContacto(@Valid @RequestBody ReservaContactoDTO dto) {
        return ResponseEntity.ok(reservaContactoService.registarContacto(dto));
    }

    @GetMapping("/comprador/{compradorId}")
    public ResponseEntity<List<ReservaContactoDTO>> listarPorComprador(@PathVariable Long compradorId) {
        return ResponseEntity.ok(reservaContactoService.listarPorComprador(compradorId));
    }

    @GetMapping("/produtor/{produtorId}")
    public ResponseEntity<List<ReservaContactoDTO>> listarPorProdutor(@PathVariable Long produtorId) {
        return ResponseEntity.ok(reservaContactoService.listarPorProdutor(produtorId));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'GESTOR_ASSOCIACAO', 'COMPRADOR', 'PRODUTOR')")
    public ResponseEntity<ReservaContactoDTO> atualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoNegocio novoEstado) {
        return ResponseEntity.ok(reservaContactoService.atualizarEstado(id, novoEstado));
    }
}

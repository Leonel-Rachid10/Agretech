package com.agritech.dondo.controller;

import com.agritech.dondo.dto.ReservaContactoDTO;
import com.agritech.dondo.model.EstadoNegocio;
import com.agritech.dondo.service.ReservaContactoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
    public ResponseEntity<ReservaContactoDTO> registarContacto(
            @Valid @RequestBody ReservaContactoDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(reservaContactoService.registarContacto(dto, username));
    }

    /**
     * Listar negociações/reservas visíveis para o utilizador autenticado
     */
    @GetMapping
    public ResponseEntity<List<ReservaContactoDTO>> listarVisiveis(
            @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(reservaContactoService.listarVisiveisPara(userDetails.getUsername()));
    }

    @GetMapping("/comprador/{compradorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReservaContactoDTO>> listarPorComprador(@PathVariable Long compradorId) {
        return ResponseEntity.ok(reservaContactoService.listarPorComprador(compradorId));
    }

    @GetMapping("/produtor/{produtorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReservaContactoDTO>> listarPorProdutor(@PathVariable Long produtorId) {
        return ResponseEntity.ok(reservaContactoService.listarPorProdutor(produtorId));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ADMIN', 'COMPRADOR', 'PRODUTOR')")
    public ResponseEntity<ReservaContactoDTO> atualizarEstado(
            @PathVariable Long id,
            @RequestParam EstadoNegocio novoEstado,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        return ResponseEntity.ok(reservaContactoService.atualizarEstado(id, novoEstado, username));
    }
}

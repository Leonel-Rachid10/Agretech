package com.agritech.dondo.controller;

import com.agritech.dondo.dto.UtilizadorDTO;
import com.agritech.dondo.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/utilizadores")
public class UtilizadorController {

    private final AuthService authService;

    public UtilizadorController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UtilizadorDTO>> listarTodos() {
        return ResponseEntity.ok(authService.listarTodosDTO());
    }
}

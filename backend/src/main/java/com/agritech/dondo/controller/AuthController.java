package com.agritech.dondo.controller;

import com.agritech.dondo.dto.LoginRequest;
import com.agritech.dondo.dto.LoginResponse;
import com.agritech.dondo.dto.RegistoUtilizadorDTO;
import com.agritech.dondo.model.Utilizador;
import com.agritech.dondo.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/registo")
    public ResponseEntity<LoginResponse> registar(@Valid @RequestBody RegistoUtilizadorDTO dto) {
        return ResponseEntity.ok(authService.registar(dto));
    }

    @GetMapping("/me")
    public ResponseEntity<Utilizador> obterAtual(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(authService.obterPorTelemovel(userDetails.getUsername()));
    }
}

package com.agritech.dondo.service;

import com.agritech.dondo.config.JwtUtil;
import com.agritech.dondo.dto.LoginRequest;
import com.agritech.dondo.dto.LoginResponse;
import com.agritech.dondo.dto.RegistoUtilizadorDTO;
import com.agritech.dondo.model.Utilizador;
import com.agritech.dondo.repository.UtilizadorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UtilizadorRepository utilizadorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UtilizadorRepository utilizadorRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.utilizadorRepository = utilizadorRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {
        Utilizador utilizador = utilizadorRepository.findByTelemovel(request.getTelemovel())
                .orElseThrow(() -> new IllegalArgumentException("Telemóvel ou senha incorretos"));

        if (!passwordEncoder.matches(request.getSenha(), utilizador.getSenhaHash())) {
            throw new IllegalArgumentException("Telemóvel ou senha incorretos");
        }

        String token = jwtUtil.generateToken(
                utilizador.getTelemovel(),
                utilizador.getPerfil().name(),
                utilizador.getNome(),
                utilizador.getId()
        );

        return new LoginResponse(
                token,
                utilizador.getId(),
                utilizador.getNome(),
                utilizador.getTelemovel(),
                utilizador.getPerfil()
        );
    }

    @Transactional
    public LoginResponse registar(RegistoUtilizadorDTO dto) {
        if (utilizadorRepository.existsByTelemovel(dto.getTelemovel())) {
            throw new IllegalArgumentException("Já existe um utilizador registado com este número de telemóvel");
        }

        Utilizador novo = new Utilizador(
                dto.getNome(),
                dto.getTelemovel(),
                passwordEncoder.encode(dto.getSenha()),
                dto.getPerfil()
        );

        Utilizador salvo = utilizadorRepository.save(novo);

        String token = jwtUtil.generateToken(
                salvo.getTelemovel(),
                salvo.getPerfil().name(),
                salvo.getNome(),
                salvo.getId()
        );

        return new LoginResponse(
                token,
                salvo.getId(),
                salvo.getNome(),
                salvo.getTelemovel(),
                salvo.getPerfil()
        );
    }

    public Utilizador obterPorTelemovel(String telemovel) {
        return utilizadorRepository.findByTelemovel(telemovel)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));
    }
}

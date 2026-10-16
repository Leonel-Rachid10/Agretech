package com.agritech.dondo.service;

import com.agritech.dondo.config.JwtUtil;
import com.agritech.dondo.dto.LoginRequest;
import com.agritech.dondo.dto.LoginResponse;
import com.agritech.dondo.dto.RegistoUtilizadorDTO;
import com.agritech.dondo.model.Utilizador;
import com.agritech.dondo.model.Produtor;
import com.agritech.dondo.model.Perfil;
import com.agritech.dondo.repository.UtilizadorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UtilizadorRepository utilizadorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ProdutorService produtorService;

    public AuthService(UtilizadorRepository utilizadorRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       ProdutorService produtorService) {
        this.utilizadorRepository = utilizadorRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.produtorService = produtorService;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        Utilizador utilizador = utilizadorRepository.findByNome(request.getNome())
                .orElseThrow(() -> new IllegalArgumentException("Telemóvel ou senha incorretos"));

        if (!passwordEncoder.matches(request.getSenha(), utilizador.getSenhaHash())) {
            throw new IllegalArgumentException("Telemóvel ou senha incorretos");
        }

        Produtor produtor = utilizador.getPerfil() == Perfil.PRODUTOR
                ? produtorService.obterOuCriarParaUtilizador(utilizador)
                : null;

        String token = jwtUtil.generateToken(
                utilizador.getNome(),
                utilizador.getPerfil().name(),
                utilizador.getNome(),
                utilizador.getId()
        );

        return new LoginResponse(
                token,
                utilizador.getId(),
                utilizador.getNome(),
                utilizador.getTelemovel(),
                utilizador.getPerfil(),
                produtor != null ? produtor.getId() : null
        );
    }

    @Transactional
    public LoginResponse registar(RegistoUtilizadorDTO dto) {
        if (dto.getPerfil() == Perfil.ADMIN) {
            throw new IllegalArgumentException("Não é possível criar uma conta de administrador pelo registo público.");
        }
        if (utilizadorRepository.existsByNome(dto.getNome()) || utilizadorRepository.existsByTelemovel(dto.getTelemovel())) {
            throw new IllegalArgumentException("Já existe uma conta com este nome ou número de telemóvel");
        }

        Utilizador novo = new Utilizador(
                dto.getNome(),
                dto.getTelemovel(),
                passwordEncoder.encode(dto.getSenha()),
                dto.getPerfil()
        );
        novo.setProvincia(dto.getProvincia());
        novo.setDistrito(dto.getDistrito());
        novo.setBairro(dto.getBairro());

        Utilizador salvo = utilizadorRepository.save(novo);
        Produtor produtor = salvo.getPerfil() == Perfil.PRODUTOR
                ? produtorService.obterOuCriarParaUtilizador(salvo)
                : null;

        String token = jwtUtil.generateToken(
                salvo.getNome(),
                salvo.getPerfil().name(),
                salvo.getNome(),
                salvo.getId()
        );

        return new LoginResponse(
                token,
                salvo.getId(),
                salvo.getNome(),
                salvo.getTelemovel(),
                salvo.getPerfil(),
                produtor != null ? produtor.getId() : null
        );
    }

    public Utilizador obterPorNome(String nome) {
        Utilizador utilizador = utilizadorRepository.findByNome(nome)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado"));
        if (utilizador.getPerfil() == Perfil.PRODUTOR) {
            utilizador.setProdutorId(produtorService.obterOuCriarParaUtilizador(utilizador).getId());
        }
        return utilizador;
    }
}

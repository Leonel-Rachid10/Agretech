package com.agritech.dondo.service;

import com.agritech.dondo.model.Utilizador;
import com.agritech.dondo.repository.UtilizadorRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilizadorRepository utilizadorRepository;

    public CustomUserDetailsService(UtilizadorRepository utilizadorRepository) {
        this.utilizadorRepository = utilizadorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String telemovel) throws UsernameNotFoundException {
        Utilizador utilizador = utilizadorRepository.findByTelemovel(telemovel)
                .orElseThrow(() -> new UsernameNotFoundException("Utilizador não encontrado com telemóvel: " + telemovel));

        return new User(
                utilizador.getTelemovel(),
                utilizador.getSenhaHash(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + utilizador.getPerfil().name()))
        );
    }
}

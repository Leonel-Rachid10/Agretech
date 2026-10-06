package com.agritech.dondo.repository;

import com.agritech.dondo.model.Utilizador;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


public interface UtilizadorRepository extends JpaRepository<Utilizador, Long> {
    Optional<Utilizador> findByNome(String nome);
    boolean existsByNome(String nome);
}

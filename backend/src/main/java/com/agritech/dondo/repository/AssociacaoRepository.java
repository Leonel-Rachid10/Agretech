package com.agritech.dondo.repository;

import com.agritech.dondo.model.Associacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface AssociacaoRepository extends JpaRepository<Associacao, Long> {
    List<Associacao> findByLocalidadeContainingIgnoreCase(String localidade);
}

package com.agritech.dondo.repository;

import com.agritech.dondo.model.Produtor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProdutorRepository extends JpaRepository<Produtor, Long> {
    List<Produtor> findByAssociacaoId(Long associacaoId);
    Optional<Produtor> findByTelemovel(String telemovel);
}

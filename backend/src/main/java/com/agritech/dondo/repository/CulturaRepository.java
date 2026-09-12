package com.agritech.dondo.repository;

import com.agritech.dondo.model.Cultura;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface CulturaRepository extends JpaRepository<Cultura, Long> {
    Optional<Cultura> findByNomeIgnoreCase(String nome);
    List<Cultura> findAllByOrderByNomeAsc();
}

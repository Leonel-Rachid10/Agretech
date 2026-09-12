package com.agritech.dondo.repository;

import com.agritech.dondo.model.ReservaContacto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ReservaContactoRepository extends JpaRepository<ReservaContacto, Long> {
    List<ReservaContacto> findByCompradorIdOrderByDataContactoDesc(Long compradorId);
    List<ReservaContacto> findByLoteProdutorIdOrderByDataContactoDesc(Long produtorId);
}

package com.agritech.dondo.repository;

import com.agritech.dondo.model.SincronizacaoLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SincronizacaoLogRepository extends JpaRepository<SincronizacaoLog, Long> {
    List<SincronizacaoLog> findAllByOrderByDataRecepcaoDesc();
}

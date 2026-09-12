package com.agritech.dondo.service;

import com.agritech.dondo.model.Cultura;
import com.agritech.dondo.repository.CulturaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CulturaService {

    private final CulturaRepository culturaRepository;

    public CulturaService(CulturaRepository culturaRepository) {
        this.culturaRepository = culturaRepository;
    }

    public List<Cultura> listarTodas() {
        return culturaRepository.findAllByOrderByNomeAsc();
    }

    public Cultura buscarPorId(Long id) {
        return culturaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cultura não encontrada com ID: " + id));
    }

    @Transactional
    public Cultura criar(Cultura cultura) {
        return culturaRepository.save(cultura);
    }
}

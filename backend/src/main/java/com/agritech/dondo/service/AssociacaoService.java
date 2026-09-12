package com.agritech.dondo.service;

import com.agritech.dondo.dto.AssociacaoDTO;
import com.agritech.dondo.model.Associacao;
import com.agritech.dondo.model.Utilizador;
import com.agritech.dondo.repository.AssociacaoRepository;
import com.agritech.dondo.repository.UtilizadorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssociacaoService {

    private final AssociacaoRepository associacaoRepository;
    private final UtilizadorRepository utilizadorRepository;

    public AssociacaoService(AssociacaoRepository associacaoRepository,
                             UtilizadorRepository utilizadorRepository) {
        this.associacaoRepository = associacaoRepository;
        this.utilizadorRepository = utilizadorRepository;
    }

    public List<AssociacaoDTO> listarTodas() {
        return associacaoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public AssociacaoDTO buscarPorId(Long id) {
        Associacao associacao = associacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Associação não encontrada com ID: " + id));
        return toDTO(associacao);
    }

    @Transactional
    public AssociacaoDTO criar(AssociacaoDTO dto) {
        Associacao associacao = new Associacao();
        preencherDados(associacao, dto);
        return toDTO(associacaoRepository.save(associacao));
    }

    @Transactional
    public AssociacaoDTO atualizar(Long id, AssociacaoDTO dto) {
        Associacao associacao = associacaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Associação não encontrada com ID: " + id));
        preencherDados(associacao, dto);
        return toDTO(associacaoRepository.save(associacao));
    }

    @Transactional
    public void remover(Long id) {
        if (!associacaoRepository.existsById(id)) {
            throw new IllegalArgumentException("Associação não encontrada com ID: " + id);
        }
        associacaoRepository.deleteById(id);
    }

    private void preencherDados(Associacao entity, AssociacaoDTO dto) {
        entity.setNome(dto.getNome());
        entity.setLocalidade(dto.getLocalidade());
        entity.setPovoado(dto.getPovoado());
        entity.setContactoPrincipal(dto.getContactoPrincipal());
        entity.setPontoFocalNome(dto.getPontoFocalNome());
        entity.setPontoFocalTelemovel(dto.getPontoFocalTelemovel());

        if (dto.getGestorId() != null) {
            Utilizador gestor = utilizadorRepository.findById(dto.getGestorId()).orElse(null);
            entity.setGestor(gestor);
        }
    }

    public AssociacaoDTO toDTO(Associacao entity) {
        AssociacaoDTO dto = new AssociacaoDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setLocalidade(entity.getLocalidade());
        dto.setPovoado(entity.getPovoado());
        dto.setContactoPrincipal(entity.getContactoPrincipal());
        dto.setPontoFocalNome(entity.getPontoFocalNome());
        dto.setPontoFocalTelemovel(entity.getPontoFocalTelemovel());
        if (entity.getGestor() != null) {
            dto.setGestorId(entity.getGestor().getId());
        }
        return dto;
    }
}

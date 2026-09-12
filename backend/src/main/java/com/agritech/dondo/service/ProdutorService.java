package com.agritech.dondo.service;

import com.agritech.dondo.dto.ProdutorDTO;
import com.agritech.dondo.model.Associacao;
import com.agritech.dondo.model.Produtor;
import com.agritech.dondo.repository.AssociacaoRepository;
import com.agritech.dondo.repository.ProdutorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProdutorService {

    private final ProdutorRepository produtorRepository;
    private final AssociacaoRepository associacaoRepository;

    public ProdutorService(ProdutorRepository produtorRepository,
                           AssociacaoRepository associacaoRepository) {
        this.produtorRepository = produtorRepository;
        this.associacaoRepository = associacaoRepository;
    }

    public List<ProdutorDTO> listarTodos() {
        return produtorRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProdutorDTO> listarPorAssociacao(Long associacaoId) {
        return produtorRepository.findByAssociacaoId(associacaoId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ProdutorDTO buscarPorId(Long id) {
        Produtor produtor = produtorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produtor não encontrado com ID: " + id));
        return toDTO(produtor);
    }

    @Transactional
    public ProdutorDTO criar(ProdutorDTO dto) {
        Associacao associacao = associacaoRepository.findById(dto.getAssociacaoId())
                .orElseThrow(() -> new IllegalArgumentException("Associação não encontrada com ID: " + dto.getAssociacaoId()));

        Produtor produtor = new Produtor(
                dto.getNome(),
                dto.getTelemovel(),
                associacao,
                dto.getLocalizacaoDetalhada()
        );

        return toDTO(produtorRepository.save(produtor));
    }

    @Transactional
    public ProdutorDTO atualizar(Long id, ProdutorDTO dto) {
        Produtor produtor = produtorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produtor não encontrado com ID: " + id));

        Associacao associacao = associacaoRepository.findById(dto.getAssociacaoId())
                .orElseThrow(() -> new IllegalArgumentException("Associação não encontrada com ID: " + dto.getAssociacaoId()));

        produtor.setNome(dto.getNome());
        produtor.setTelemovel(dto.getTelemovel());
        produtor.setAssociacao(associacao);
        produtor.setLocalizacaoDetalhada(dto.getLocalizacaoDetalhada());

        return toDTO(produtorRepository.save(produtor));
    }

    @Transactional
    public void remover(Long id) {
        if (!produtorRepository.existsById(id)) {
            throw new IllegalArgumentException("Produtor não encontrado com ID: " + id);
        }
        produtorRepository.deleteById(id);
    }

    public ProdutorDTO toDTO(Produtor entity) {
        ProdutorDTO dto = new ProdutorDTO();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setTelemovel(entity.getTelemovel());
        dto.setAssociacaoId(entity.getAssociacao().getId());
        dto.setAssociacaoNome(entity.getAssociacao().getNome());
        dto.setAssociacaoLocalidade(entity.getAssociacao().getLocalidade());
        dto.setLocalizacaoDetalhada(entity.getLocalizacaoDetalhada());
        return dto;
    }
}

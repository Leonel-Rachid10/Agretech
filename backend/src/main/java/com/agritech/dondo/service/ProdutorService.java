package com.agritech.dondo.service;

import com.agritech.dondo.dto.ProdutorDTO;
import com.agritech.dondo.model.Associacao;
import com.agritech.dondo.model.Produtor;
import com.agritech.dondo.model.Utilizador;
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

    @Transactional
    public Produtor obterOuCriarParaUtilizador(Utilizador utilizador) {
        return produtorRepository.findByUtilizadorId(utilizador.getId()).orElseGet(() -> {
            Produtor produtor = produtorRepository.findByTelemovel(utilizador.getTelemovel())
                    .orElseGet(() -> new Produtor(utilizador.getNome(), utilizador.getTelemovel(), null, null));

            if (produtor.getUtilizador() != null && !produtor.getUtilizador().getId().equals(utilizador.getId())) {
                throw new IllegalStateException("Este perfil agrícola já está ligado a outra conta.");
            }

            produtor.setUtilizador(utilizador);
            produtor.setNome(utilizador.getNome());
            produtor.setTelemovel(utilizador.getTelemovel());
            if (utilizador.getProvincia() != null && !utilizador.getProvincia().isBlank()) {
                produtor.setProvincia(utilizador.getProvincia());
            }
            if (utilizador.getDistrito() != null && !utilizador.getDistrito().isBlank()) {
                produtor.setDistrito(utilizador.getDistrito());
            }
            if (utilizador.getBairro() != null && !utilizador.getBairro().isBlank()) {
                produtor.setBairro(utilizador.getBairro());
            }
            return produtorRepository.save(produtor);
        });
    }

    public ProdutorDTO buscarPorId(Long id) {
        Produtor produtor = produtorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produtor não encontrado com ID: " + id));
        return toDTO(produtor);
    }

    @Transactional
    public ProdutorDTO criar(ProdutorDTO dto) {
        Associacao associacao = null;
        if (dto.getAssociacaoId() != null) {
            associacao = associacaoRepository.findById(dto.getAssociacaoId()).orElse(null);
        }

        Produtor produtor = new Produtor(
                dto.getNome(),
                dto.getTelemovel(),
                associacao,
                dto.getLocalizacaoDetalhada()
        );
        produtor.setProvincia(dto.getProvincia());
        produtor.setDistrito(dto.getDistrito());
        produtor.setBairro(dto.getBairro());

        return toDTO(produtorRepository.save(produtor));
    }

    @Transactional
    public ProdutorDTO atualizar(Long id, ProdutorDTO dto) {
        Produtor produtor = produtorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produtor não encontrado com ID: " + id));

        if (dto.getAssociacaoId() != null) {
            associacaoRepository.findById(dto.getAssociacaoId()).ifPresent(produtor::setAssociacao);
        }

        produtor.setNome(dto.getNome());
        produtor.setTelemovel(dto.getTelemovel());
        if (dto.getProvincia() != null) produtor.setProvincia(dto.getProvincia());
        if (dto.getDistrito() != null) produtor.setDistrito(dto.getDistrito());
        if (dto.getBairro() != null) produtor.setBairro(dto.getBairro());
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
        if (entity.getAssociacao() != null) {
            dto.setAssociacaoId(entity.getAssociacao().getId());
            dto.setAssociacaoNome(entity.getAssociacao().getNome());
            dto.setAssociacaoLocalidade(entity.getAssociacao().getLocalidade());
        }
        dto.setProvincia(entity.getProvincia() != null ? entity.getProvincia() : "Sofala");
        dto.setDistrito(entity.getDistrito());
        dto.setBairro(entity.getBairro());
        dto.setLocalizacaoDetalhada(entity.getLocalizacaoDetalhada());
        return dto;
    }
}

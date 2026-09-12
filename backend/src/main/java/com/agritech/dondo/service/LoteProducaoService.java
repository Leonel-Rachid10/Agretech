package com.agritech.dondo.service;

import com.agritech.dondo.dto.LoteProducaoDTO;
import com.agritech.dondo.model.Cultura;
import com.agritech.dondo.model.EstadoLote;
import com.agritech.dondo.model.LoteProducao;
import com.agritech.dondo.model.Produtor;
import com.agritech.dondo.repository.CulturaRepository;
import com.agritech.dondo.repository.LoteProducaoRepository;
import com.agritech.dondo.repository.ProdutorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoteProducaoService {

    private final LoteProducaoRepository loteProducaoRepository;
    private final ProdutorRepository produtorRepository;
    private final CulturaRepository culturaRepository;

    public LoteProducaoService(LoteProducaoRepository loteProducaoRepository,
                               ProdutorRepository produtorRepository,
                               CulturaRepository culturaRepository) {
        this.loteProducaoRepository = loteProducaoRepository;
        this.produtorRepository = produtorRepository;
        this.culturaRepository = culturaRepository;
    }

    public List<LoteProducaoDTO> pesquisarCatalogo(Long culturaId, String localidade, EstadoLote estado, BigDecimal quantidadeMinima) {
        return loteProducaoRepository.pesquisarCatalogo(culturaId, localidade, estado, quantidadeMinima).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<LoteProducaoDTO> listarTodos() {
        return loteProducaoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<LoteProducaoDTO> listarPorProdutor(Long produtorId) {
        return loteProducaoRepository.findByProdutorId(produtorId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<LoteProducaoDTO> listarPorAssociacao(Long associacaoId) {
        return loteProducaoRepository.findByProdutorAssociacaoId(associacaoId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public LoteProducaoDTO buscarPorId(Long id) {
        LoteProducao lote = loteProducaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lote de produção não encontrado com ID: " + id));
        return toDTO(lote);
    }

    @Transactional
    public LoteProducaoDTO criar(LoteProducaoDTO dto) {
        Produtor produtor = produtorRepository.findById(dto.getProdutorId())
                .orElseThrow(() -> new IllegalArgumentException("Produtor não encontrado com ID: " + dto.getProdutorId()));

        Cultura cultura = culturaRepository.findById(dto.getCulturaId())
                .orElseThrow(() -> new IllegalArgumentException("Cultura não encontrada com ID: " + dto.getCulturaId()));

        LoteProducao lote = new LoteProducao();
        lote.setProdutor(produtor);
        lote.setCultura(cultura);
        lote.setQuantidadeEstimada(dto.getQuantidadeEstimada());
        lote.setDataSementeira(dto.getDataSementeira());
        lote.setDataColheitaPrevista(dto.getDataColheitaPrevista());
        lote.setEstado(dto.getEstado() != null ? dto.getEstado() : EstadoLote.EM_CRESCIMENTO);
        lote.setPrecoPorUnidade(dto.getPrecoPorUnidade());
        lote.setObservacoes(dto.getObservacoes());

        return toDTO(loteProducaoRepository.save(lote));
    }

    @Transactional
    public LoteProducaoDTO atualizar(Long id, LoteProducaoDTO dto) {
        LoteProducao lote = loteProducaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lote de produção não encontrado com ID: " + id));

        if (dto.getCulturaId() != null) {
            Cultura cultura = culturaRepository.findById(dto.getCulturaId())
                    .orElseThrow(() -> new IllegalArgumentException("Cultura não encontrada com ID: " + dto.getCulturaId()));
            lote.setCultura(cultura);
        }

        if (dto.getProdutorId() != null) {
            Produtor produtor = produtorRepository.findById(dto.getProdutorId())
                    .orElseThrow(() -> new IllegalArgumentException("Produtor não encontrado com ID: " + dto.getProdutorId()));
            lote.setProdutor(produtor);
        }

        if (dto.getQuantidadeEstimada() != null) {
            lote.setQuantidadeEstimada(dto.getQuantidadeEstimada());
        }
        if (dto.getDataSementeira() != null) {
            lote.setDataSementeira(dto.getDataSementeira());
        }
        if (dto.getDataColheitaPrevista() != null) {
            lote.setDataColheitaPrevista(dto.getDataColheitaPrevista());
        }
        if (dto.getEstado() != null) {
            lote.setEstado(dto.getEstado());
        }
        if (dto.getPrecoPorUnidade() != null) {
            lote.setPrecoPorUnidade(dto.getPrecoPorUnidade());
        }
        if (dto.getObservacoes() != null) {
            lote.setObservacoes(dto.getObservacoes());
        }

        return toDTO(loteProducaoRepository.save(lote));
    }

    @Transactional
    public LoteProducaoDTO atualizarEstado(Long id, EstadoLote novoEstado) {
        LoteProducao lote = loteProducaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lote de produção não encontrado com ID: " + id));
        lote.setEstado(novoEstado);
        return toDTO(loteProducaoRepository.save(lote));
    }

    @Transactional
    public void remover(Long id) {
        if (!loteProducaoRepository.existsById(id)) {
            throw new IllegalArgumentException("Lote não encontrado com ID: " + id);
        }
        loteProducaoRepository.deleteById(id);
    }

    public LoteProducaoDTO toDTO(LoteProducao entity) {
        LoteProducaoDTO dto = new LoteProducaoDTO();
        dto.setId(entity.getId());
        dto.setProdutorId(entity.getProdutor().getId());
        dto.setProdutorNome(entity.getProdutor().getNome());
        dto.setProdutorTelemovel(entity.getProdutor().getTelemovel());

        if (entity.getProdutor().getAssociacao() != null) {
            dto.setAssociacaoNome(entity.getProdutor().getAssociacao().getNome());
            dto.setAssociacaoLocalidade(entity.getProdutor().getAssociacao().getLocalidade());
        }

        dto.setCulturaId(entity.getCultura().getId());
        dto.setCulturaNome(entity.getCultura().getNome());
        dto.setCulturaCategoria(entity.getCultura().getCategoria());
        dto.setUnidadeMedida(entity.getCultura().getUnidadeMedida());

        dto.setQuantidadeEstimada(entity.getQuantidadeEstimada());
        dto.setDataSementeira(entity.getDataSementeira());
        dto.setDataColheitaPrevista(entity.getDataColheitaPrevista());
        dto.setEstado(entity.getEstado());
        dto.setPrecoPorUnidade(entity.getPrecoPorUnidade());
        dto.setObservacoes(entity.getObservacoes());
        dto.setDataAtualizacao(entity.getDataAtualizacao());

        return dto;
    }
}

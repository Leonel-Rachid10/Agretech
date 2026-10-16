package com.agritech.dondo.service;

import com.agritech.dondo.dto.LoteProducaoDTO;
import com.agritech.dondo.model.Cultura;
import com.agritech.dondo.model.EstadoLote;
import com.agritech.dondo.model.LoteProducao;
import com.agritech.dondo.model.Produtor;
import com.agritech.dondo.model.Perfil;
import com.agritech.dondo.model.Utilizador;
import com.agritech.dondo.repository.CulturaRepository;
import com.agritech.dondo.repository.LoteProducaoRepository;
import com.agritech.dondo.repository.ProdutorRepository;
import com.agritech.dondo.repository.UtilizadorRepository;
import org.springframework.security.access.AccessDeniedException;
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
    private final UtilizadorRepository utilizadorRepository;

    public LoteProducaoService(LoteProducaoRepository loteProducaoRepository,
                               ProdutorRepository produtorRepository,
                               CulturaRepository culturaRepository,
                               UtilizadorRepository utilizadorRepository) {
        this.loteProducaoRepository = loteProducaoRepository;
        this.produtorRepository = produtorRepository;
        this.culturaRepository = culturaRepository;
        this.utilizadorRepository = utilizadorRepository;
    }

    public List<LoteProducaoDTO> pesquisarCatalogo(Long culturaId, String provincia, String localidade, EstadoLote estado, BigDecimal quantidadeMinima) {
        return loteProducaoRepository.pesquisarCatalogo(culturaId, provincia, localidade, estado, quantidadeMinima).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<LoteProducaoDTO> listarTodos() {
        return loteProducaoRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<LoteProducaoDTO> listarVisiveisPara(String nomeUtilizador) {
        Utilizador utilizador = obterUtilizador(nomeUtilizador);
        if (utilizador.getPerfil() == Perfil.PRODUTOR) {
            Produtor produtor = produtorRepository.findByUtilizadorId(utilizador.getId())
                    .orElseThrow(() -> new IllegalStateException("Perfil agrícola não encontrado para esta conta."));
            return listarPorProdutor(produtor.getId());
        }
        return listarTodos();
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

        return criarParaProdutor(dto, produtor);
    }

    @Transactional
    public LoteProducaoDTO criar(LoteProducaoDTO dto, String nomeUtilizador) {
        Utilizador utilizador = obterUtilizador(nomeUtilizador);
        Produtor produtor;

        if (utilizador.getPerfil() == Perfil.PRODUTOR) {
            produtor = produtorRepository.findByUtilizadorId(utilizador.getId())
                    .orElseThrow(() -> new IllegalStateException("Perfil agrícola não encontrado para esta conta."));
        } else {
            if (dto.getProdutorId() == null) {
                throw new IllegalArgumentException("Selecione o agricultor para este produto.");
            }
            produtor = produtorRepository.findById(dto.getProdutorId())
                    .orElseThrow(() -> new IllegalArgumentException("Agricultor não encontrado com ID: " + dto.getProdutorId()));
        }

        return criarParaProdutor(dto, produtor);
    }

    private LoteProducaoDTO criarParaProdutor(LoteProducaoDTO dto, Produtor produtor) {

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
        return atualizarInterno(id, dto, true);
    }

    @Transactional
    public LoteProducaoDTO atualizar(Long id, LoteProducaoDTO dto, String nomeUtilizador) {
        Utilizador utilizador = obterUtilizador(nomeUtilizador);
        LoteProducao lote = obterLote(id);
        validarProprietarioOuAdministrador(lote, utilizador);
        return atualizarInterno(id, dto, utilizador.getPerfil() == Perfil.ADMIN);
    }

    private LoteProducaoDTO atualizarInterno(Long id, LoteProducaoDTO dto, boolean podeTrocarProdutor) {
        LoteProducao lote = loteProducaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lote de produção não encontrado com ID: " + id));

        if (dto.getCulturaId() != null) {
            Cultura cultura = culturaRepository.findById(dto.getCulturaId())
                    .orElseThrow(() -> new IllegalArgumentException("Cultura não encontrada com ID: " + dto.getCulturaId()));
            lote.setCultura(cultura);
        }

        if (podeTrocarProdutor && dto.getProdutorId() != null) {
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
    public LoteProducaoDTO atualizarEstado(Long id, EstadoLote novoEstado, String nomeUtilizador) {
        Utilizador utilizador = obterUtilizador(nomeUtilizador);
        LoteProducao lote = obterLote(id);
        validarProprietarioOuAdministrador(lote, utilizador);
        lote.setEstado(novoEstado);
        return toDTO(loteProducaoRepository.save(lote));
    }

    private Utilizador obterUtilizador(String nomeUtilizador) {
        return utilizadorRepository.findByNome(nomeUtilizador)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado."));
    }

    private LoteProducao obterLote(Long id) {
        return loteProducaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Lote de produção não encontrado com ID: " + id));
    }

    private void validarProprietarioOuAdministrador(LoteProducao lote, Utilizador utilizador) {
        if (utilizador.getPerfil() == Perfil.ADMIN) return;

        boolean eDono = lote.getProdutor().getUtilizador() != null
                && lote.getProdutor().getUtilizador().getId().equals(utilizador.getId());
        if (!eDono) {
            throw new AccessDeniedException("Só pode alterar os seus próprios produtos.");
        }
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
        dto.setProvincia(entity.getProdutor().getProvincia() != null
                ? entity.getProdutor().getProvincia() : "Sofala");
        dto.setDistrito(entity.getProdutor().getDistrito());
        dto.setBairro(entity.getProdutor().getBairro());

        if (entity.getProdutor().getAssociacao() != null) {
            dto.setAssociacaoNome(entity.getProdutor().getAssociacao().getNome());
            dto.setAssociacaoLocalidade(entity.getProdutor().getAssociacao().getLocalidade());
        } else if (entity.getProdutor().getDistrito() != null) {
            String loc = entity.getProdutor().getDistrito();
            if (entity.getProdutor().getBairro() != null && !entity.getProdutor().getBairro().isBlank()) {
                loc += " (" + entity.getProdutor().getBairro() + ")";
            }
            if (entity.getProdutor().getProvincia() != null && !entity.getProdutor().getProvincia().isBlank()) {
                loc = entity.getProdutor().getProvincia() + " · " + loc;
            }
            dto.setAssociacaoLocalidade(loc);
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

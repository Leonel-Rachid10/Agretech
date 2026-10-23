package com.agritech.dondo.service;

import com.agritech.dondo.dto.ReservaContactoDTO;
import com.agritech.dondo.model.*;
import com.agritech.dondo.repository.LoteProducaoRepository;
import com.agritech.dondo.repository.ProdutorRepository;
import com.agritech.dondo.repository.ReservaContactoRepository;
import com.agritech.dondo.repository.UtilizadorRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReservaContactoService {

    private final ReservaContactoRepository reservaContactoRepository;
    private final LoteProducaoRepository loteProducaoRepository;
    private final UtilizadorRepository utilizadorRepository;
    private final ProdutorRepository produtorRepository;

    public ReservaContactoService(ReservaContactoRepository reservaContactoRepository,
                                  LoteProducaoRepository loteProducaoRepository,
                                  UtilizadorRepository utilizadorRepository,
                                  ProdutorRepository produtorRepository) {
        this.reservaContactoRepository = reservaContactoRepository;
        this.loteProducaoRepository = loteProducaoRepository;
        this.utilizadorRepository = utilizadorRepository;
        this.produtorRepository = produtorRepository;
    }

    @Transactional
    public ReservaContactoDTO registarContacto(ReservaContactoDTO dto) {
        return registarContacto(dto, null);
    }

    @Transactional
    public ReservaContactoDTO registarContacto(ReservaContactoDTO dto, String nomeUtilizador) {
        LoteProducao lote = loteProducaoRepository.findById(dto.getLoteId())
                .orElseThrow(() -> new IllegalArgumentException("Lote não encontrado com ID: " + dto.getLoteId()));

        Utilizador comprador;
        if (dto.getCompradorId() != null) {
            comprador = utilizadorRepository.findById(dto.getCompradorId())
                    .orElseThrow(() -> new IllegalArgumentException("Comprador não encontrado com ID: " + dto.getCompradorId()));
        } else if (nomeUtilizador != null) {
            comprador = utilizadorRepository.findByNome(nomeUtilizador)
                    .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado: " + nomeUtilizador));
        } else {
            throw new IllegalArgumentException("Identificação do comprador é obrigatória.");
        }

        ReservaContacto reserva = new ReservaContacto(
                lote,
                comprador,
                dto.getEstadoNegocio() != null ? dto.getEstadoNegocio() : EstadoNegocio.INICIADO
        );

        return toDTO(reservaContactoRepository.save(reserva));
    }

    public List<ReservaContactoDTO> listarTodos() {
        return reservaContactoRepository.findAllByOrderByDataContactoDesc().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ReservaContactoDTO> listarVisiveisPara(String nomeUtilizador) {
        Utilizador utilizador = utilizadorRepository.findByNome(nomeUtilizador)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado: " + nomeUtilizador));

        if (utilizador.getPerfil() == Perfil.ADMIN) {
            return listarTodos();
        }

        if (utilizador.getPerfil() == Perfil.PRODUTOR) {
            Produtor produtor = produtorRepository.findByUtilizadorId(utilizador.getId())
                    .orElseGet(() -> produtorRepository.findByTelemovel(utilizador.getTelemovel()).orElse(null));
            if (produtor != null) {
                return listarPorProdutor(produtor.getId());
            }
            return Collections.emptyList();
        }

        return listarPorComprador(utilizador.getId());
    }

    public List<ReservaContactoDTO> listarPorComprador(Long compradorId) {
        return reservaContactoRepository.findByCompradorIdOrderByDataContactoDesc(compradorId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<ReservaContactoDTO> listarPorProdutor(Long produtorId) {
        return reservaContactoRepository.findByLoteProdutorIdOrderByDataContactoDesc(produtorId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservaContactoDTO atualizarEstado(Long id, EstadoNegocio estado) {
        return atualizarEstado(id, estado, null);
    }

    @Transactional
    public ReservaContactoDTO atualizarEstado(Long id, EstadoNegocio estado, String nomeUtilizador) {
        ReservaContacto reserva = reservaContactoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registo de contacto não encontrado com ID: " + id));

        if (nomeUtilizador != null) {
            Utilizador utilizador = utilizadorRepository.findByNome(nomeUtilizador)
                    .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado: " + nomeUtilizador));
            validarParticipanteOuAdministrador(reserva, utilizador);
        }

        reserva.setEstadoNegocio(estado);
        return toDTO(reservaContactoRepository.save(reserva));
    }

    private void validarParticipanteOuAdministrador(ReservaContacto reserva, Utilizador utilizador) {
        if (utilizador.getPerfil() == Perfil.ADMIN) return;

        boolean eComprador = reserva.getComprador() != null
                && reserva.getComprador().getId().equals(utilizador.getId());

        boolean eProdutor = false;
        if (reserva.getLote() != null && reserva.getLote().getProdutor() != null) {
            Produtor prod = reserva.getLote().getProdutor();
            if (prod.getUtilizador() != null && prod.getUtilizador().getId().equals(utilizador.getId())) {
                eProdutor = true;
            } else if (prod.getTelemovel() != null && prod.getTelemovel().equals(utilizador.getTelemovel())) {
                eProdutor = true;
            }
        }

        if (!eComprador && !eProdutor) {
            throw new AccessDeniedException("Não tem permissão para alterar esta negociação.");
        }
    }

    public ReservaContactoDTO toDTO(ReservaContacto entity) {
        ReservaContactoDTO dto = new ReservaContactoDTO();
        dto.setId(entity.getId());
        dto.setLoteId(entity.getLote().getId());
        dto.setCulturaNome(entity.getLote().getCultura() != null ? entity.getLote().getCultura().getNome() : "");
        dto.setQuantidadeEstimada(entity.getLote().getQuantidadeEstimada());
        dto.setPrecoPorUnidade(entity.getLote().getPrecoPorUnidade());
        dto.setUnidadeMedida(entity.getLote().getCultura() != null ? entity.getLote().getCultura().getUnidadeMedida() : "KG");
        dto.setLoteEstado(entity.getLote().getEstado());

        if (entity.getLote().getProdutor() != null) {
            dto.setProdutorNome(entity.getLote().getProdutor().getNome());
            dto.setProdutorTelemovel(entity.getLote().getProdutor().getTelemovel());
            String loc = Stream.of(
                    entity.getLote().getProdutor().getProvincia(),
                    entity.getLote().getProdutor().getDistrito(),
                    entity.getLote().getProdutor().getBairro()
            ).filter(s -> s != null && !s.isBlank()).collect(Collectors.joining(" · "));
            dto.setLocalidade(loc);
        }

        if (entity.getComprador() != null) {
            dto.setCompradorId(entity.getComprador().getId());
            dto.setCompradorNome(entity.getComprador().getNome());
            dto.setCompradorTelemovel(entity.getComprador().getTelemovel());
        }

        dto.setDataContacto(entity.getDataContacto());
        dto.setEstadoNegocio(entity.getEstadoNegocio());
        return dto;
    }
}

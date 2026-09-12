package com.agritech.dondo.service;

import com.agritech.dondo.dto.ReservaContactoDTO;
import com.agritech.dondo.model.EstadoNegocio;
import com.agritech.dondo.model.LoteProducao;
import com.agritech.dondo.model.ReservaContacto;
import com.agritech.dondo.model.Utilizador;
import com.agritech.dondo.repository.LoteProducaoRepository;
import com.agritech.dondo.repository.ReservaContactoRepository;
import com.agritech.dondo.repository.UtilizadorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservaContactoService {

    private final ReservaContactoRepository reservaContactoRepository;
    private final LoteProducaoRepository loteProducaoRepository;
    private final UtilizadorRepository utilizadorRepository;

    public ReservaContactoService(ReservaContactoRepository reservaContactoRepository,
                                  LoteProducaoRepository loteProducaoRepository,
                                  UtilizadorRepository utilizadorRepository) {
        this.reservaContactoRepository = reservaContactoRepository;
        this.loteProducaoRepository = loteProducaoRepository;
        this.utilizadorRepository = utilizadorRepository;
    }

    @Transactional
    public ReservaContactoDTO registarContacto(ReservaContactoDTO dto) {
        LoteProducao lote = loteProducaoRepository.findById(dto.getLoteId())
                .orElseThrow(() -> new IllegalArgumentException("Lote não encontrado com ID: " + dto.getLoteId()));

        Utilizador comprador = utilizadorRepository.findById(dto.getCompradorId())
                .orElseThrow(() -> new IllegalArgumentException("Comprador não encontrado com ID: " + dto.getCompradorId()));

        ReservaContacto reserva = new ReservaContacto(
                lote,
                comprador,
                dto.getEstadoNegocio() != null ? dto.getEstadoNegocio() : EstadoNegocio.INICIADO
        );

        return toDTO(reservaContactoRepository.save(reserva));
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
        ReservaContacto reserva = reservaContactoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Registo de contacto não encontrado com ID: " + id));
        reserva.setEstadoNegocio(estado);
        return toDTO(reservaContactoRepository.save(reserva));
    }

    public ReservaContactoDTO toDTO(ReservaContacto entity) {
        ReservaContactoDTO dto = new ReservaContactoDTO();
        dto.setId(entity.getId());
        dto.setLoteId(entity.getLote().getId());
        dto.setCulturaNome(entity.getLote().getCultura().getNome());
        dto.setProdutorNome(entity.getLote().getProdutor().getNome());
        dto.setProdutorTelemovel(entity.getLote().getProdutor().getTelemovel());

        dto.setCompradorId(entity.getComprador().getId());
        dto.setCompradorNome(entity.getComprador().getNome());
        dto.setCompradorTelemovel(entity.getComprador().getTelemovel());

        dto.setDataContacto(entity.getDataContacto());
        dto.setEstadoNegocio(entity.getEstadoNegocio());
        return dto;
    }
}

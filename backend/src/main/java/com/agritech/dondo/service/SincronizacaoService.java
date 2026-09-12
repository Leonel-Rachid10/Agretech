package com.agritech.dondo.service;

import com.agritech.dondo.dto.LoteProducaoDTO;
import com.agritech.dondo.dto.ProdutorDTO;
import com.agritech.dondo.dto.SyncBatchDTO;
import com.agritech.dondo.dto.SyncResultDTO;
import com.agritech.dondo.model.Produtor;
import com.agritech.dondo.repository.ProdutorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class SincronizacaoService {

    private static final Logger log = LoggerFactory.getLogger(SincronizacaoService.class);

    private final ProdutorService produtorService;
    private final ProdutorRepository produtorRepository;
    private final LoteProducaoService loteProducaoService;

    public SincronizacaoService(ProdutorService produtorService,
                                ProdutorRepository produtorRepository,
                                LoteProducaoService loteProducaoService) {
        this.produtorService = produtorService;
        this.produtorRepository = produtorRepository;
        this.loteProducaoService = loteProducaoService;
    }

    @Transactional
    public SyncResultDTO processarLote(SyncBatchDTO batch) {
        SyncResultDTO resultado = new SyncResultDTO();
        int produtoresSalvos = 0;
        int lotesSalvos = 0;

        log.info("Recebida sincronização offline do ponto focal/telemóvel: {} às {}",
                batch.getPontoFocalTelemovel(), batch.getClientTimestamp());

        // 1. Processar novos produtores criados offline
        if (batch.getProdutoresNovos() != null) {
            for (ProdutorDTO pDto : batch.getProdutoresNovos()) {
                try {
                    Optional<Produtor> existente = produtorRepository.findByTelemovel(pDto.getTelemovel());
                    Long produtorId;
                    if (existente.isPresent()) {
                        produtorId = existente.get().getId();
                    } else {
                        ProdutorDTO criado = produtorService.criar(pDto);
                        produtorId = criado.getId();
                        produtoresSalvos++;
                    }

                    if (pDto.getId() != null) {
                        resultado.getIdMappings().put("produtor_" + pDto.getId(), produtorId);
                    }
                } catch (Exception e) {
                    resultado.getErros().add("Erro ao sincronizar produtor '" + pDto.getNome() + "': " + e.getMessage());
                }
            }
        }

        // 2. Processar novos lotes de produção criados offline
        if (batch.getLotesNovos() != null) {
            for (LoteProducaoDTO lDto : batch.getLotesNovos()) {
                try {
                    // Mapear ID do produtor se tiver sido criado offline
                    if (lDto.getProdutorId() != null && resultado.getIdMappings().containsKey("produtor_" + lDto.getProdutorId())) {
                        lDto.setProdutorId(resultado.getIdMappings().get("produtor_" + lDto.getProdutorId()));
                    }

                    LoteProducaoDTO salvo = loteProducaoService.criar(lDto);
                    lotesSalvos++;

                    if (lDto.getClientUuid() != null) {
                        resultado.getIdMappings().put(lDto.getClientUuid(), salvo.getId());
                    }
                } catch (Exception e) {
                    resultado.getErros().add("Erro ao sincronizar novo lote de cultura ID " + lDto.getCulturaId() + ": " + e.getMessage());
                }
            }
        }

        // 3. Processar lotes atualizados offline
        if (batch.getLotesAtualizados() != null) {
            for (LoteProducaoDTO lDto : batch.getLotesAtualizados()) {
                try {
                    if (lDto.getId() != null) {
                        loteProducaoService.atualizar(lDto.getId(), lDto);
                        lotesSalvos++;
                    }
                } catch (Exception e) {
                    resultado.getErros().add("Erro ao atualizar lote ID " + lDto.getId() + ": " + e.getMessage());
                }
            }
        }

        resultado.setProdutoresSincronizados(produtoresSalvos);
        resultado.setLotesSincronizados(lotesSalvos);
        resultado.setSucesso(resultado.getErros().isEmpty());

        return resultado;
    }
}

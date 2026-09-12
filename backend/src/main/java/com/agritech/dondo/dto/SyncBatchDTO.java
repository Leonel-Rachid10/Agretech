package com.agritech.dondo.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SyncBatchDTO {

    private LocalDateTime clientTimestamp;
    private String pontoFocalTelemovel;
    private List<ProdutorDTO> produtoresNovos = new ArrayList<>();
    private List<LoteProducaoDTO> lotesNovos = new ArrayList<>();
    private List<LoteProducaoDTO> lotesAtualizados = new ArrayList<>();

    public SyncBatchDTO() {}

    public LocalDateTime getClientTimestamp() { return clientTimestamp; }
    public void setClientTimestamp(LocalDateTime clientTimestamp) { this.clientTimestamp = clientTimestamp; }

    public String getPontoFocalTelemovel() { return pontoFocalTelemovel; }
    public void setPontoFocalTelemovel(String pontoFocalTelemovel) { this.pontoFocalTelemovel = pontoFocalTelemovel; }

    public List<ProdutorDTO> getProdutoresNovos() { return produtoresNovos; }
    public void setProdutoresNovos(List<ProdutorDTO> produtoresNovos) { this.produtoresNovos = produtoresNovos; }

    public List<LoteProducaoDTO> getLotesNovos() { return lotesNovos; }
    public void setLotesNovos(List<LoteProducaoDTO> lotesNovos) { this.lotesNovos = lotesNovos; }

    public List<LoteProducaoDTO> getLotesAtualizados() { return lotesAtualizados; }
    public void setLotesAtualizados(List<LoteProducaoDTO> lotesAtualizados) { this.lotesAtualizados = lotesAtualizados; }
}

package com.agritech.dondo.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SyncResultDTO {

    private boolean sucesso;
    private int produtoresSincronizados;
    private int lotesSincronizados;
    private List<String> erros = new ArrayList<>();
    private Map<String, Long> idMappings = new HashMap<>();
    private LocalDateTime serverTimestamp;

    public SyncResultDTO() {
        this.serverTimestamp = LocalDateTime.now();
    }

    public boolean isSucesso() { return sucesso; }
    public void setSucesso(boolean sucesso) { this.sucesso = sucesso; }

    public int getProdutoresSincronizados() { return produtoresSincronizados; }
    public void setProdutoresSincronizados(int produtoresSincronizados) { this.produtoresSincronizados = produtoresSincronizados; }

    public int getLotesSincronizados() { return lotesSincronizados; }
    public void setLotesSincronizados(int lotesSincronizados) { this.lotesSincronizados = lotesSincronizados; }

    public List<String> getErros() { return erros; }
    public void setErros(List<String> erros) { this.erros = erros; }

    public Map<String, Long> getIdMappings() { return idMappings; }
    public void setIdMappings(Map<String, Long> idMappings) { this.idMappings = idMappings; }

    public LocalDateTime getServerTimestamp() { return serverTimestamp; }
    public void setServerTimestamp(LocalDateTime serverTimestamp) { this.serverTimestamp = serverTimestamp; }
}

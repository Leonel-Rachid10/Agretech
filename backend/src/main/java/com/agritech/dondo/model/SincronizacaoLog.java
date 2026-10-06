package com.agritech.dondo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sincronizacao_log")
public class SincronizacaoLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ponto_focal_telemovel")
    private String pontoFocalTelemovel;

    @Column(name = "client_timestamp")
    private LocalDateTime clientTimestamp;

    @Column(name = "data_recepcao", nullable = false)
    private LocalDateTime dataRecepcao = LocalDateTime.now();

    @Column(name = "produtores_sincronizados")
    private int produtoresSincronizados;

    @Column(name = "lotes_sincronizados")
    private int lotesSincronizados;

    @Column(name = "sucesso")
    private boolean sucesso;

    @Column(name = "erros", columnDefinition = "TEXT")
    private String erros;

    // Constructors
    public SincronizacaoLog() {}

    public SincronizacaoLog(String pontoFocalTelemovel, LocalDateTime clientTimestamp,
                             int produtoresSincronizados, int lotesSincronizados,
                             boolean sucesso, String erros) {
        this.pontoFocalTelemovel = pontoFocalTelemovel;
        this.clientTimestamp = clientTimestamp;
        this.dataRecepcao = LocalDateTime.now();
        this.produtoresSincronizados = produtoresSincronizados;
        this.lotesSincronizados = lotesSincronizados;
        this.sucesso = sucesso;
        this.erros = erros;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public String getPontoFocalTelemovel() { return pontoFocalTelemovel; }
    public void setPontoFocalTelemovel(String pontoFocalTelemovel) { this.pontoFocalTelemovel = pontoFocalTelemovel; }
    public LocalDateTime getClientTimestamp() { return clientTimestamp; }
    public void setClientTimestamp(LocalDateTime clientTimestamp) { this.clientTimestamp = clientTimestamp; }
    public LocalDateTime getDataRecepcao() { return dataRecepcao; }
    public void setDataRecepcao(LocalDateTime dataRecepcao) { this.dataRecepcao = dataRecepcao; }
    public int getProdutoresSincronizados() { return produtoresSincronizados; }
    public void setProdutoresSincronizados(int produtoresSincronizados) { this.produtoresSincronizados = produtoresSincronizados; }
    public int getLotesSincronizados() { return lotesSincronizados; }
    public void setLotesSincronizados(int lotesSincronizados) { this.lotesSincronizados = lotesSincronizados; }
    public boolean isSucesso() { return sucesso; }
    public void setSucesso(boolean sucesso) { this.sucesso = sucesso; }
    public String getErros() { return erros; }
    public void setErros(String erros) { this.erros = erros; }
}

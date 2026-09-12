package com.agritech.dondo.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "lote_producao")
public class LoteProducao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produtor_id", nullable = false)
    private Produtor produtor;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cultura_id", nullable = false)
    private Cultura cultura;

    @Column(name = "quantidade_estimada", nullable = false, precision = 10, scale = 2)
    private BigDecimal quantidadeEstimada;

    @Column(name = "data_sementeira")
    private LocalDate dataSementeira;

    @Column(name = "data_colheita_prevista", nullable = false)
    private LocalDate dataColheitaPrevista;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoLote estado = EstadoLote.EM_CRESCIMENTO;

    @Column(name = "preco_por_unidade", precision = 10, scale = 2)
    private BigDecimal precoPorUnidade;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @PrePersist
    @PreUpdate
    public void onUpdate() {
        this.dataAtualizacao = LocalDateTime.now();
    }

    public LoteProducao() {}

    public LoteProducao(Produtor produtor, Cultura cultura, BigDecimal quantidadeEstimada,
                        LocalDate dataSementeira, LocalDate dataColheitaPrevista,
                        EstadoLote estado, BigDecimal precoPorUnidade, String observacoes) {
        this.produtor = produtor;
        this.cultura = cultura;
        this.quantidadeEstimada = quantidadeEstimada;
        this.dataSementeira = dataSementeira;
        this.dataColheitaPrevista = dataColheitaPrevista;
        this.estado = estado != null ? estado : EstadoLote.EM_CRESCIMENTO;
        this.precoPorUnidade = precoPorUnidade;
        this.observacoes = observacoes;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Produtor getProdutor() { return produtor; }
    public void setProdutor(Produtor produtor) { this.produtor = produtor; }

    public Cultura getCultura() { return cultura; }
    public void setCultura(Cultura cultura) { this.cultura = cultura; }

    public BigDecimal getQuantidadeEstimada() { return quantidadeEstimada; }
    public void setQuantidadeEstimada(BigDecimal quantidadeEstimada) { this.quantidadeEstimada = quantidadeEstimada; }

    public LocalDate getDataSementeira() { return dataSementeira; }
    public void setDataSementeira(LocalDate dataSementeira) { this.dataSementeira = dataSementeira; }

    public LocalDate getDataColheitaPrevista() { return dataColheitaPrevista; }
    public void setDataColheitaPrevista(LocalDate dataColheitaPrevista) { this.dataColheitaPrevista = dataColheitaPrevista; }

    public EstadoLote getEstado() { return estado; }
    public void setEstado(EstadoLote estado) { this.estado = estado; }

    public BigDecimal getPrecoPorUnidade() { return precoPorUnidade; }
    public void setPrecoPorUnidade(BigDecimal precoPorUnidade) { this.precoPorUnidade = precoPorUnidade; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public LocalDateTime getDataAtualizacao() { return dataAtualizacao; }
    public void setDataAtualizacao(LocalDateTime dataAtualizacao) { this.dataAtualizacao = dataAtualizacao; }
}

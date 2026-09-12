package com.agritech.dondo.dto;

import com.agritech.dondo.model.EstadoLote;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LoteProducaoDTO {

    private Long id;

    @NotNull(message = "O ID do produtor é obrigatório")
    private Long produtorId;
    private String produtorNome;
    private String produtorTelemovel;

    private String associacaoNome;
    private String associacaoLocalidade;

    @NotNull(message = "O ID da cultura é obrigatório")
    private Long culturaId;
    private String culturaNome;
    private String culturaCategoria;
    private String unidadeMedida;

    @NotNull(message = "A quantidade estimada é obrigatória")
    private BigDecimal quantidadeEstimada;

    private LocalDate dataSementeira;

    @NotNull(message = "A data de colheita prevista é obrigatória")
    private LocalDate dataColheitaPrevista;

    private EstadoLote estado = EstadoLote.EM_CRESCIMENTO;
    private BigDecimal precoPorUnidade;
    private String observacoes;
    private LocalDateTime dataAtualizacao;

    // UUID do lado do cliente para sincronização offline
    private String clientUuid;

    public LoteProducaoDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProdutorId() { return produtorId; }
    public void setProdutorId(Long produtorId) { this.produtorId = produtorId; }

    public String getProdutorNome() { return produtorNome; }
    public void setProdutorNome(String produtorNome) { this.produtorNome = produtorNome; }

    public String getProdutorTelemovel() { return produtorTelemovel; }
    public void setProdutorTelemovel(String produtorTelemovel) { this.produtorTelemovel = produtorTelemovel; }

    public String getAssociacaoNome() { return associacaoNome; }
    public void setAssociacaoNome(String associacaoNome) { this.associacaoNome = associacaoNome; }

    public String getAssociacaoLocalidade() { return associacaoLocalidade; }
    public void setAssociacaoLocalidade(String associacaoLocalidade) { this.associacaoLocalidade = associacaoLocalidade; }

    public Long getCulturaId() { return culturaId; }
    public void setCulturaId(Long culturaId) { this.culturaId = culturaId; }

    public String getCulturaNome() { return culturaNome; }
    public void setCulturaNome(String culturaNome) { this.culturaNome = culturaNome; }

    public String getCulturaCategoria() { return culturaCategoria; }
    public void setCulturaCategoria(String culturaCategoria) { this.culturaCategoria = culturaCategoria; }

    public String getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(String unidadeMedida) { this.unidadeMedida = unidadeMedida; }

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

    public String getClientUuid() { return clientUuid; }
    public void setClientUuid(String clientUuid) { this.clientUuid = clientUuid; }
}

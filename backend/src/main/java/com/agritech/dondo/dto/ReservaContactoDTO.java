package com.agritech.dondo.dto;

import com.agritech.dondo.model.EstadoLote;
import com.agritech.dondo.model.EstadoNegocio;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ReservaContactoDTO {

    private Long id;

    @NotNull(message = "O ID do lote é obrigatório")
    private Long loteId;

    private String culturaNome;
    private BigDecimal quantidadeEstimada;
    private String unidadeMedida;
    private BigDecimal precoPorUnidade;
    private EstadoLote loteEstado;
    private String localidade;

    private String produtorNome;
    private String produtorTelemovel;

    private Long compradorId;
    private String compradorNome;
    private String compradorTelemovel;

    private LocalDateTime dataContacto;
    private EstadoNegocio estadoNegocio = EstadoNegocio.INICIADO;

    public ReservaContactoDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLoteId() { return loteId; }
    public void setLoteId(Long loteId) { this.loteId = loteId; }

    public String getCulturaNome() { return culturaNome; }
    public void setCulturaNome(String culturaNome) { this.culturaNome = culturaNome; }

    public BigDecimal getQuantidadeEstimada() { return quantidadeEstimada; }
    public void setQuantidadeEstimada(BigDecimal quantidadeEstimada) { this.quantidadeEstimada = quantidadeEstimada; }

    public String getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(String unidadeMedida) { this.unidadeMedida = unidadeMedida; }

    public BigDecimal getPrecoPorUnidade() { return precoPorUnidade; }
    public void setPrecoPorUnidade(BigDecimal precoPorUnidade) { this.precoPorUnidade = precoPorUnidade; }

    public EstadoLote getLoteEstado() { return loteEstado; }
    public void setLoteEstado(EstadoLote loteEstado) { this.loteEstado = loteEstado; }

    public String getLocalidade() { return localidade; }
    public void setLocalidade(String localidade) { this.localidade = localidade; }

    public String getProdutorNome() { return produtorNome; }
    public void setProdutorNome(String produtorNome) { this.produtorNome = produtorNome; }

    public String getProdutorTelemovel() { return produtorTelemovel; }
    public void setProdutorTelemovel(String produtorTelemovel) { this.produtorTelemovel = produtorTelemovel; }

    public Long getCompradorId() { return compradorId; }
    public void setCompradorId(Long compradorId) { this.compradorId = compradorId; }

    public String getCompradorNome() { return compradorNome; }
    public void setCompradorNome(String compradorNome) { this.compradorNome = compradorNome; }

    public String getCompradorTelemovel() { return compradorTelemovel; }
    public void setCompradorTelemovel(String compradorTelemovel) { this.compradorTelemovel = compradorTelemovel; }

    public LocalDateTime getDataContacto() { return dataContacto; }
    public void setDataContacto(LocalDateTime dataContacto) { this.dataContacto = dataContacto; }

    public EstadoNegocio getEstadoNegocio() { return estadoNegocio; }
    public void setEstadoNegocio(EstadoNegocio estadoNegocio) { this.estadoNegocio = estadoNegocio; }

    public EstadoNegocio getEstado() { return estadoNegocio; }
    public void setEstado(EstadoNegocio estado) { this.estadoNegocio = estado; }
}

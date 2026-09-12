package com.agritech.dondo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProdutorDTO {

    private Long id;

    @NotBlank(message = "O nome do produtor é obrigatório")
    private String nome;

    @NotBlank(message = "O telemóvel do produtor é obrigatório")
    private String telemovel;

    @NotNull(message = "O ID da associação é obrigatório")
    private Long associacaoId;

    private String associacaoNome;
    private String associacaoLocalidade;
    private String localizacaoDetalhada;

    public ProdutorDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelemovel() { return telemovel; }
    public void setTelemovel(String telemovel) { this.telemovel = telemovel; }

    public Long getAssociacaoId() { return associacaoId; }
    public void setAssociacaoId(Long associacaoId) { this.associacaoId = associacaoId; }

    public String getAssociacaoNome() { return associacaoNome; }
    public void setAssociacaoNome(String associacaoNome) { this.associacaoNome = associacaoNome; }

    public String getAssociacaoLocalidade() { return associacaoLocalidade; }
    public void setAssociacaoLocalidade(String associacaoLocalidade) { this.associacaoLocalidade = associacaoLocalidade; }

    public String getLocalizacaoDetalhada() { return localizacaoDetalhada; }
    public void setLocalizacaoDetalhada(String localizacaoDetalhada) { this.localizacaoDetalhada = localizacaoDetalhada; }
}

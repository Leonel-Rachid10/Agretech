package com.agritech.dondo.dto;

import jakarta.validation.constraints.NotBlank;

public class AssociacaoDTO {

    private Long id;

    @NotBlank(message = "O nome da associação é obrigatório")
    private String nome;

    @NotBlank(message = "A localidade é obrigatória (ex: Mafambisse, Chinamacondo, Dondo Sede)")
    private String localidade;

    private String povoado;
    private String contactoPrincipal;
    private Long gestorId;
    private String pontoFocalNome;
    private String pontoFocalTelemovel;

    public AssociacaoDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getLocalidade() { return localidade; }
    public void setLocalidade(String localidade) { this.localidade = localidade; }

    public String getPovoado() { return povoado; }
    public void setPovoado(String povoado) { this.povoado = povoado; }

    public String getContactoPrincipal() { return contactoPrincipal; }
    public void setContactoPrincipal(String contactoPrincipal) { this.contactoPrincipal = contactoPrincipal; }

    public Long getGestorId() { return gestorId; }
    public void setGestorId(Long gestorId) { this.gestorId = gestorId; }

    public String getPontoFocalNome() { return pontoFocalNome; }
    public void setPontoFocalNome(String pontoFocalNome) { this.pontoFocalNome = pontoFocalNome; }

    public String getPontoFocalTelemovel() { return pontoFocalTelemovel; }
    public void setPontoFocalTelemovel(String pontoFocalTelemovel) { this.pontoFocalTelemovel = pontoFocalTelemovel; }
}

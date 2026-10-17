package com.agritech.dondo.dto;

import com.agritech.dondo.model.Perfil;
import java.time.LocalDateTime;

public class UtilizadorDTO {

    private Long id;
    private String nome;
    private String telemovel;
    private Perfil perfil;
    private String provincia;
    private String distrito;
    private String bairro;
    private LocalDateTime dataCriacao;

    public UtilizadorDTO() {}

    public UtilizadorDTO(Long id, String nome, String telemovel, Perfil perfil, String provincia, String distrito, String bairro, LocalDateTime dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.telemovel = telemovel;
        this.perfil = perfil;
        this.provincia = provincia;
        this.distrito = distrito;
        this.bairro = bairro;
        this.dataCriacao = dataCriacao;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelemovel() { return telemovel; }
    public void setTelemovel(String telemovel) { this.telemovel = telemovel; }

    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }

    public String getProvincia() { return provincia; }
    public void setProvincia(String provincia) { this.provincia = provincia; }

    public String getDistrito() { return distrito; }
    public void setDistrito(String distrito) { this.distrito = distrito; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}

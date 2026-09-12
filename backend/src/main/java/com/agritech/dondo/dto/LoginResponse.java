package com.agritech.dondo.dto;

import com.agritech.dondo.model.Perfil;

public class LoginResponse {

    private String token;
    private String tipo = "Bearer";
    private Long id;
    private String nome;
    private String telemovel;
    private Perfil perfil;

    public LoginResponse() {}

    public LoginResponse(String token, Long id, String nome, String telemovel, Perfil perfil) {
        this.token = token;
        this.id = id;
        this.nome = nome;
        this.telemovel = telemovel;
        this.perfil = perfil;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelemovel() { return telemovel; }
    public void setTelemovel(String telemovel) { this.telemovel = telemovel; }

    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }
}

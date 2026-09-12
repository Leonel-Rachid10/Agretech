package com.agritech.dondo.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "O número de telemóvel é obrigatório")
    private String telemovel;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;

    public LoginRequest() {}

    public LoginRequest(String telemovel, String senha) {
        this.telemovel = telemovel;
        this.senha = senha;
    }

    public String getTelemovel() { return telemovel; }
    public void setTelemovel(String telemovel) { this.telemovel = telemovel; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}

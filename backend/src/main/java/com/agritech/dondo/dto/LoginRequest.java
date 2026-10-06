package com.agritech.dondo.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "O número de telemóvel é obrigatório")
    private String nome;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;

    public LoginRequest() {}

    public LoginRequest(String nome, String senha) {
        this.nome = nome;
        this.senha = senha;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}

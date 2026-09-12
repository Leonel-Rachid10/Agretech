package com.agritech.dondo.dto;

import com.agritech.dondo.model.Perfil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegistoUtilizadorDTO {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O telemóvel é obrigatório")
    private String telemovel;

    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 4, message = "A senha deve conter no mínimo 4 caracteres")
    private String senha;

    @NotNull(message = "O perfil é obrigatório")
    private Perfil perfil;

    public RegistoUtilizadorDTO() {}

    public RegistoUtilizadorDTO(String nome, String telemovel, String senha, Perfil perfil) {
        this.nome = nome;
        this.telemovel = telemovel;
        this.senha = senha;
        this.perfil = perfil;
    }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelemovel() { return telemovel; }
    public void setTelemovel(String telemovel) { this.telemovel = telemovel; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }
}

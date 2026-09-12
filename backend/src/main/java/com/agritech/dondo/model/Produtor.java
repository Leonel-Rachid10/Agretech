package com.agritech.dondo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "produtor")
public class Produtor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 20)
    private String telemovel;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "associacao_id", nullable = false)
    private Associacao associacao;

    @Column(name = "localizacao_detalhada", columnDefinition = "TEXT")
    private String localizacaoDetalhada;

    @Column(name = "data_registo", updatable = false)
    private LocalDateTime dataRegisto;

    @PrePersist
    public void prePersist() {
        if (this.dataRegisto == null) {
            this.dataRegisto = LocalDateTime.now();
        }
    }

    public Produtor() {}

    public Produtor(String nome, String telemovel, Associacao associacao, String localizacaoDetalhada) {
        this.nome = nome;
        this.telemovel = telemovel;
        this.associacao = associacao;
        this.localizacaoDetalhada = localizacaoDetalhada;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTelemovel() { return telemovel; }
    public void setTelemovel(String telemovel) { this.telemovel = telemovel; }

    public Associacao getAssociacao() { return associacao; }
    public void setAssociacao(Associacao associacao) { this.associacao = associacao; }

    public String getLocalizacaoDetalhada() { return localizacaoDetalhada; }
    public void setLocalizacaoDetalhada(String localizacaoDetalhada) { this.localizacaoDetalhada = localizacaoDetalhada; }

    public LocalDateTime getDataRegisto() { return dataRegisto; }
    public void setDataRegisto(LocalDateTime dataRegisto) { this.dataRegisto = dataRegisto; }
}

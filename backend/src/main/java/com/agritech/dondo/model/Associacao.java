package com.agritech.dondo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "associacao")
public class Associacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(nullable = false, length = 100)
    private String localidade;

    @Column(length = 100)
    private String povoado;

    @Column(name = "contacto_principal", length = 20)
    private String contactoPrincipal;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "gestor_id")
    private Utilizador gestor;

    @Column(name = "ponto_focal_nome", length = 100)
    private String pontoFocalNome;

    @Column(name = "ponto_focal_telemovel", length = 20)
    private String pontoFocalTelemovel;

    @Column(name = "data_registo", updatable = false)
    private LocalDateTime dataRegisto;

    @PrePersist
    public void prePersist() {
        if (this.dataRegisto == null) {
            this.dataRegisto = LocalDateTime.now();
        }
    }

    public Associacao() {}

    public Associacao(String nome, String localidade, String povoado, String contactoPrincipal,
                      Utilizador gestor, String pontoFocalNome, String pontoFocalTelemovel) {
        this.nome = nome;
        this.localidade = localidade;
        this.povoado = povoado;
        this.contactoPrincipal = contactoPrincipal;
        this.gestor = gestor;
        this.pontoFocalNome = pontoFocalNome;
        this.pontoFocalTelemovel = pontoFocalTelemovel;
    }

    // Getters and Setters
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

    public Utilizador getGestor() { return gestor; }
    public void setGestor(Utilizador gestor) { this.gestor = gestor; }

    public String getPontoFocalNome() { return pontoFocalNome; }
    public void setPontoFocalNome(String pontoFocalNome) { this.pontoFocalNome = pontoFocalNome; }

    public String getPontoFocalTelemovel() { return pontoFocalTelemovel; }
    public void setPontoFocalTelemovel(String pontoFocalTelemovel) { this.pontoFocalTelemovel = pontoFocalTelemovel; }

    public LocalDateTime getDataRegisto() { return dataRegisto; }
    public void setDataRegisto(LocalDateTime dataRegisto) { this.dataRegisto = dataRegisto; }
}

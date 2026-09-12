package com.agritech.dondo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cultura")
public class Cultura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Column(nullable = false, length = 50)
    private String categoria;

    @Column(name = "unidade_medida", nullable = false, length = 20)
    private String unidadeMedida = "KG";

    public Cultura() {}

    public Cultura(String nome, String categoria, String unidadeMedida) {
        this.nome = nome;
        this.categoria = categoria;
        this.unidadeMedida = unidadeMedida != null ? unidadeMedida : "KG";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public String getUnidadeMedida() { return unidadeMedida; }
    public void setUnidadeMedida(String unidadeMedida) { this.unidadeMedida = unidadeMedida; }
}

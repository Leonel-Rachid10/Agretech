package com.agritech.dondo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reserva_contacto")
public class ReservaContacto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lote_id", nullable = false)
    private LoteProducao lote;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "comprador_id", nullable = false)
    private Utilizador comprador;

    @Column(name = "data_contacto", updatable = false)
    private LocalDateTime dataContacto;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_negocio")
    private EstadoNegocio estadoNegocio = EstadoNegocio.INICIADO;

    @PrePersist
    public void prePersist() {
        if (this.dataContacto == null) {
            this.dataContacto = LocalDateTime.now();
        }
    }

    public ReservaContacto() {}

    public ReservaContacto(LoteProducao lote, Utilizador comprador, EstadoNegocio estadoNegocio) {
        this.lote = lote;
        this.comprador = comprador;
        this.estadoNegocio = estadoNegocio != null ? estadoNegocio : EstadoNegocio.INICIADO;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LoteProducao getLote() { return lote; }
    public void setLote(LoteProducao lote) { this.lote = lote; }

    public Utilizador getComprador() { return comprador; }
    public void setComprador(Utilizador comprador) { this.comprador = comprador; }

    public LocalDateTime getDataContacto() { return dataContacto; }
    public void setDataContacto(LocalDateTime dataContacto) { this.dataContacto = dataContacto; }

    public EstadoNegocio getEstadoNegocio() { return estadoNegocio; }
    public void setEstadoNegocio(EstadoNegocio estadoNegocio) { this.estadoNegocio = estadoNegocio; }
}

package com.example.gestionpolizas.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "riesgos")
public class Riesgo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La dirección del inmueble es obligatoria")
    private String direccionInmueble;

    @NotNull(message = "El estado del riesgo es obligatorio")
    @Enumerated(EnumType.STRING)
    private EstadoRiesgo estado;

    private String descripcionRiesgo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poliza_id")
    @JsonBackReference
    private Poliza poliza;

    // Constructores
    public Riesgo() {
    }

    public Riesgo(String direccionInmueble, EstadoRiesgo estado, String descripcionRiesgo) {
        this.direccionInmueble = direccionInmueble;
        this.estado = estado;
        this.descripcionRiesgo = descripcionRiesgo;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDireccionInmueble() {
        return direccionInmueble;
    }

    public void setDireccionInmueble(String direccionInmueble) {
        this.direccionInmueble = direccionInmueble;
    }

    public EstadoRiesgo getEstado() {
        return estado;
    }

    public void setEstado(EstadoRiesgo estado) {
        this.estado = estado;
    }

    public String getDescripcionRiesgo() {
        return descripcionRiesgo;
    }

    public void setDescripcionRiesgo(String descripcionRiesgo) {
        this.descripcionRiesgo = descripcionRiesgo;
    }

    public Poliza getPoliza() {
        return poliza;
    }

    public void setPoliza(Poliza poliza) {
        this.poliza = poliza;
    }
}

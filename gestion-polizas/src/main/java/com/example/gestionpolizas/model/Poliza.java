package com.example.gestionpolizas.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "polizas")
public class Poliza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El tipo de póliza es obligatorio")
    @Enumerated(EnumType.STRING)
    private TipoPoliza tipo;

    @NotNull(message = "El estado de póliza es obligatorio")
    @Enumerated(EnumType.STRING)
    private EstadoPoliza estado;

    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate fechaFin;

    @NotNull(message = "El canon mensual es obligatorio")
    private BigDecimal canonMensual;

    @NotNull(message = "La prima es obligatoria")
    private BigDecimal prima;

    @NotNull(message = "El tomador es obligatorio")
    private String tomador;

    @NotNull(message = "El asegurado es obligatorio")
    private String asegurado;

    @NotNull(message = "El beneficiario es obligatorio")
    private String beneficiario;

    @OneToMany(mappedBy = "poliza", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Riesgo> riesgos = new ArrayList<>();

    // Constructores
    public Poliza() {
    }

    public Poliza(TipoPoliza tipo, EstadoPoliza estado, LocalDate fechaInicio, LocalDate fechaFin, 
                  BigDecimal canonMensual, BigDecimal prima, String tomador, String asegurado, String beneficiario) {
        this.tipo = tipo;
        this.estado = estado;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.canonMensual = canonMensual;
        this.prima = prima;
        this.tomador = tomador;
        this.asegurado = asegurado;
        this.beneficiario = beneficiario;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoPoliza getTipo() {
        return tipo;
    }

    public void setTipo(TipoPoliza tipo) {
        this.tipo = tipo;
    }

    public EstadoPoliza getEstado() {
        return estado;
    }

    public void setEstado(EstadoPoliza estado) {
        this.estado = estado;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public BigDecimal getCanonMensual() {
        return canonMensual;
    }

    public void setCanonMensual(BigDecimal canonMensual) {
        this.canonMensual = canonMensual;
    }

    public BigDecimal getPrima() {
        return prima;
    }

    public void setPrima(BigDecimal prima) {
        this.prima = prima;
    }

    public String getTomador() {
        return tomador;
    }

    public void setTomador(String tomador) {
        this.tomador = tomador;
    }

    public String getAsegurado() {
        return asegurado;
    }

    public void setAsegurado(String asegurado) {
        this.asegurado = asegurado;
    }

    public String getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(String beneficiario) {
        this.beneficiario = beneficiario;
    }

    public List<Riesgo> getRiesgos() {
        return riesgos;
    }

    public void setRiesgos(List<Riesgo> riesgos) {
        this.riesgos = riesgos;
    }

    public void addRiesgo(Riesgo riesgo) {
        riesgos.add(riesgo);
        riesgo.setPoliza(this);
    }

    public void removeRiesgo(Riesgo riesgo) {
        riesgos.remove(riesgo);
        riesgo.setPoliza(null);
    }
}

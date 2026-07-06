package com.example.gestionpolizas.service;

import com.example.gestionpolizas.exception.BusinessValidationException;
import com.example.gestionpolizas.exception.ResourceNotFoundException;
import com.example.gestionpolizas.model.*;
import com.example.gestionpolizas.repository.PolizaRepository;
import com.example.gestionpolizas.repository.RiesgoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class PolizaService {

    @Autowired
    private PolizaRepository polizaRepository;

    @Autowired
    private RiesgoRepository riesgoRepository;

    @Autowired
    private CoreIntegrationService coreIntegrationService;

    public List<Poliza> getPolizas(TipoPoliza tipo, EstadoPoliza estado) {
        return polizaRepository.findByTipoAndEstadoOptional(tipo, estado);
    }

    public List<Riesgo> getRiesgosByPoliza(Long polizaId) {
        Poliza poliza = polizaRepository.findById(polizaId)
                .orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada con ID: " + polizaId));
        return poliza.getRiesgos();
    }

    @Transactional
    public Poliza createPoliza(Poliza poliza) {
        // Validación de tipo de póliza y riesgos asociados
        if (poliza.getTipo() == TipoPoliza.INDIVIDUAL) {
            if (poliza.getRiesgos() != null && poliza.getRiesgos().size() > 1) {
                throw new BusinessValidationException("Una póliza individual solo puede tener como máximo 1 riesgo.");
            }
        }

        // Si las fechas están presentes, calcular la prima: canonMensual * número de meses de vigencia
        if (poliza.getFechaInicio() != null && poliza.getFechaFin() != null && poliza.getCanonMensual() != null) {
            long meses = ChronoUnit.MONTHS.between(poliza.getFechaInicio(), poliza.getFechaFin());
            if (meses <= 0) {
                meses = 1; // Mínimo 1 mes si el rango es menor
            }
            BigDecimal primaCalculada = poliza.getCanonMensual().multiply(BigDecimal.valueOf(meses));
            poliza.setPrima(primaCalculada.setScale(2, RoundingMode.HALF_UP));
        }

        // Estado inicial de la póliza
        poliza.setEstado(EstadoPoliza.ACTIVA);

        // Vincular riesgos bidireccionalmente y establecer estado activo por defecto
        if (poliza.getRiesgos() != null) {
            for (Riesgo r : poliza.getRiesgos()) {
                r.setPoliza(poliza);
                r.setEstado(EstadoRiesgo.ACTIVO);
            }
        }

        Poliza savedPoliza = polizaRepository.save(poliza);

        // Notificar al CORE
        coreIntegrationService.notifyCoreOfEvent("CREACION", savedPoliza.getId());

        return savedPoliza;
    }

    @Transactional
    public Poliza renovarPoliza(Long id, Double ipc) {
        Poliza poliza = polizaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada con ID: " + id));

        // Regla de negocio: No se puede renovar una póliza cancelada
        if (poliza.getEstado() == EstadoPoliza.CANCELADA) {
            throw new BusinessValidationException("No se puede renovar una póliza cancelada.");
        }

        // Tasa de incremento (si no se proporciona ipc, asumimos un default de 5%)
        double tasaIpc = (ipc != null) ? ipc : 5.0;
        BigDecimal factor = BigDecimal.valueOf(1.0 + (tasaIpc / 100.0));

        // Incrementar canon y prima
        poliza.setCanonMensual(poliza.getCanonMensual().multiply(factor).setScale(2, RoundingMode.HALF_UP));
        poliza.setPrima(poliza.getPrima().multiply(factor).setScale(2, RoundingMode.HALF_UP));

        // Ajustar fechas para el mismo periodo de vigencia inicial
        if (poliza.getFechaInicio() != null && poliza.getFechaFin() != null) {
            LocalDate prevInicio = poliza.getFechaInicio();
            LocalDate prevFin = poliza.getFechaFin();
            long diasVigencia = ChronoUnit.DAYS.between(prevInicio, prevFin);
            
            poliza.setFechaInicio(prevFin);
            poliza.setFechaFin(prevFin.plusDays(diasVigencia));
        }

        // Cambiar estado a RENOVADA
        poliza.setEstado(EstadoPoliza.RENOVADA);

        Poliza savedPoliza = polizaRepository.save(poliza);

        // Notificar al CORE
        coreIntegrationService.notifyCoreOfEvent("ACTUALIZACION", savedPoliza.getId());

        return savedPoliza;
    }

    @Transactional
    public Poliza cancelarPoliza(Long id) {
        Poliza poliza = polizaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada con ID: " + id));

        // Cambiar estado de la póliza
        poliza.setEstado(EstadoPoliza.CANCELADA);

        // Cancelar todos sus riesgos asociados
        if (poliza.getRiesgos() != null) {
            for (Riesgo r : poliza.getRiesgos()) {
                r.setEstado(EstadoRiesgo.CANCELADO);
            }
        }

        Poliza savedPoliza = polizaRepository.save(poliza);

        // Notificar al CORE
        coreIntegrationService.notifyCoreOfEvent("ACTUALIZACION", savedPoliza.getId());

        return savedPoliza;
    }

    @Transactional
    public Riesgo addRiesgo(Long polizaId, Riesgo riesgo) {
        Poliza poliza = polizaRepository.findById(polizaId)
                .orElseThrow(() -> new ResourceNotFoundException("Póliza no encontrada con ID: " + polizaId));

        // Regla de negocio: Agregar riesgo exige validación del tipo de póliza
        // POST /polizas/{id}/riesgos - Solo si tipo = Colectiva
        if (poliza.getTipo() != TipoPoliza.COLECTIVA) {
            throw new BusinessValidationException("Solo se pueden agregar riesgos adicionales a pólizas de tipo Colectiva.");
        }

        // Guardar riesgo
        riesgo.setPoliza(poliza);
        riesgo.setEstado(EstadoRiesgo.ACTIVO);
        poliza.addRiesgo(riesgo);

        polizaRepository.save(poliza);

        // Notificar al CORE (ya que se modificó la póliza agregando un riesgo)
        coreIntegrationService.notifyCoreOfEvent("ACTUALIZACION", poliza.getId());

        // Devolver el riesgo guardado
        return poliza.getRiesgos().get(poliza.getRiesgos().size() - 1);
    }

    @Transactional
    public Riesgo cancelarRiesgo(Long riesgoId) {
        Riesgo riesgo = riesgoRepository.findById(riesgoId)
                .orElseThrow(() -> new ResourceNotFoundException("Riesgo no encontrado con ID: " + riesgoId));

        // Cancelar el riesgo
        riesgo.setEstado(EstadoRiesgo.CANCELADO);
        Riesgo savedRiesgo = riesgoRepository.save(riesgo);

        // Notificar al CORE
        if (riesgo.getPoliza() != null) {
            coreIntegrationService.notifyCoreOfEvent("ACTUALIZACION", riesgo.getPoliza().getId());
        }

        return savedRiesgo;
    }
}

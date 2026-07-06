package com.example.gestionpolizas;

import com.example.gestionpolizas.exception.BusinessValidationException;
import com.example.gestionpolizas.model.*;
import com.example.gestionpolizas.service.PolizaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.math.BigDecimal;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GestionPolizasApplicationTests {

    @Autowired
    private PolizaService polizaService;

    @Test
    void contextLoads() {
        assertNotNull(polizaService);
    }

    @Test
    void testCrearPolizaIndividualExito() {
        Poliza poliza = new Poliza();
        poliza.setTipo(TipoPoliza.INDIVIDUAL);
        poliza.setEstado(EstadoPoliza.ACTIVA);
        poliza.setFechaInicio(LocalDate.now());
        poliza.setFechaFin(LocalDate.now().plusMonths(12));
        poliza.setCanonMensual(BigDecimal.valueOf(1000.00));
        poliza.setTomador("Tomador Juan");
        poliza.setAsegurado("Asegurado Pedro");
        poliza.setBeneficiario("Beneficiario Lucas");

        Riesgo riesgo = new Riesgo("Calle 123", EstadoRiesgo.ACTIVO, "Riesgo Casa");
        poliza.addRiesgo(riesgo);

        Poliza guardada = polizaService.createPoliza(poliza);

        assertNotNull(guardada.getId());
        assertEquals(EstadoPoliza.ACTIVA, guardada.getEstado());
        assertEquals(1, guardada.getRiesgos().size());
        assertEquals(BigDecimal.valueOf(12000.00).setScale(2), guardada.getPrima());
    }

    @Test
    void testCrearPolizaIndividualConMultiplesRiesgosFalla() {
        Poliza poliza = new Poliza();
        poliza.setTipo(TipoPoliza.INDIVIDUAL);
        poliza.setEstado(EstadoPoliza.ACTIVA);
        poliza.setFechaInicio(LocalDate.now());
        poliza.setFechaFin(LocalDate.now().plusMonths(12));
        poliza.setCanonMensual(BigDecimal.valueOf(1000.00));
        poliza.setTomador("Juan");
        poliza.setAsegurado("Pedro");
        poliza.setBeneficiario("Lucas");

        poliza.addRiesgo(new Riesgo("Calle 1", EstadoRiesgo.ACTIVO, "Riesgo 1"));
        poliza.addRiesgo(new Riesgo("Calle 2", EstadoRiesgo.ACTIVO, "Riesgo 2"));

        assertThrows(BusinessValidationException.class, () -> {
            polizaService.createPoliza(poliza);
        });
    }

    @Test
    void testRenovarPolizaExitoEIncrementoIPC() {
        Poliza poliza = new Poliza();
        poliza.setTipo(TipoPoliza.INDIVIDUAL);
        poliza.setEstado(EstadoPoliza.ACTIVA);
        poliza.setFechaInicio(LocalDate.of(2026, 1, 1));
        poliza.setFechaFin(LocalDate.of(2026, 12, 31));
        poliza.setCanonMensual(BigDecimal.valueOf(1000.00));
        poliza.setTomador("Juan");
        poliza.setAsegurado("Pedro");
        poliza.setBeneficiario("Lucas");
        poliza.addRiesgo(new Riesgo("Calle 1", EstadoRiesgo.ACTIVO, "Riesgo 1"));

        Poliza guardada = polizaService.createPoliza(poliza);

        // Renovar con 5% de IPC
        Poliza renovada = polizaService.renovarPoliza(guardada.getId(), 5.0);

        assertEquals(EstadoPoliza.RENOVADA, renovada.getEstado());
        // 1000 * 1.05 = 1050
        assertEquals(BigDecimal.valueOf(1050.00).setScale(2), renovada.getCanonMensual());
        
        BigDecimal expectedPrima = guardada.getPrima().multiply(BigDecimal.valueOf(1.05)).setScale(2, java.math.RoundingMode.HALF_UP);
        assertEquals(expectedPrima, renovada.getPrima());
    }

    @Test
    void testRenovarPolizaCanceladaFalla() {
        Poliza poliza = new Poliza();
        poliza.setTipo(TipoPoliza.INDIVIDUAL);
        poliza.setEstado(EstadoPoliza.ACTIVA);
        poliza.setFechaInicio(LocalDate.now());
        poliza.setFechaFin(LocalDate.now().plusMonths(6));
        poliza.setCanonMensual(BigDecimal.valueOf(500.00));
        poliza.setTomador("Juan");
        poliza.setAsegurado("Pedro");
        poliza.setBeneficiario("Lucas");

        Poliza guardada = polizaService.createPoliza(poliza);
        polizaService.cancelarPoliza(guardada.getId());

        assertThrows(BusinessValidationException.class, () -> {
            polizaService.renovarPoliza(guardada.getId(), 4.5);
        });
    }

    @Test
    void testCancelarPolizaCancelaTodosLosRiesgos() {
        Poliza poliza = new Poliza();
        poliza.setTipo(TipoPoliza.COLECTIVA);
        poliza.setEstado(EstadoPoliza.ACTIVA);
        poliza.setFechaInicio(LocalDate.now());
        poliza.setFechaFin(LocalDate.now().plusMonths(6));
        poliza.setCanonMensual(BigDecimal.valueOf(800.00));
        poliza.setTomador("Juan");
        poliza.setAsegurado("Pedro");
        poliza.setBeneficiario("Lucas");

        poliza.addRiesgo(new Riesgo("Calle A", EstadoRiesgo.ACTIVO, "Riesgo A"));
        poliza.addRiesgo(new Riesgo("Calle B", EstadoRiesgo.ACTIVO, "Riesgo B"));

        Poliza guardada = polizaService.createPoliza(poliza);
        assertEquals(2, guardada.getRiesgos().size());

        Poliza cancelada = polizaService.cancelarPoliza(guardada.getId());

        assertEquals(EstadoPoliza.CANCELADA, cancelada.getEstado());
        for (Riesgo r : cancelada.getRiesgos()) {
            assertEquals(EstadoRiesgo.CANCELADO, r.getEstado());
        }
    }
}

package com.example.gestionpolizas.controller;

import com.example.gestionpolizas.model.*;
import com.example.gestionpolizas.service.PolizaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping
public class PolizaController {

    @Autowired
    private PolizaService polizaService;

    // 1. GET /polizas - Listar pólizas por "tipo" y "estado" (opcionales)
    @GetMapping("/polizas")
    public ResponseEntity<List<Poliza>> getPolizas(
            @RequestParam(required = false) TipoPoliza tipo,
            @RequestParam(required = false) EstadoPoliza estado) {
        List<Poliza> polizas = polizaService.getPolizas(tipo, estado);
        return ResponseEntity.ok(polizas);
    }

    // 2. GET /polizas/{id}/riesgos - Listar riesgos asociados a la póliza
    @GetMapping("/polizas/{id}/riesgos")
    public ResponseEntity<List<Riesgo>> getRiesgosByPoliza(@PathVariable Long id) {
        List<Riesgo> riesgos = polizaService.getRiesgosByPoliza(id);
        return ResponseEntity.ok(riesgos);
    }

    // Endpoint auxiliar: POST /polizas - Crear póliza (para pruebas)
    @PostMapping("/polizas")
    public ResponseEntity<Poliza> createPoliza(@Valid @RequestBody Poliza poliza) {
        Poliza creada = polizaService.createPoliza(poliza);
        return new ResponseEntity<>(creada, HttpStatus.CREATED);
    }

    // 3. POST /polizas/{id}/renovar - Renovar póliza (incremento por IPC)
    @PostMapping("/polizas/{id}/renovar")
    public ResponseEntity<Poliza> renovarPoliza(
            @PathVariable Long id,
            @RequestParam(required = false) Double ipc) {
        Poliza renovada = polizaService.renovarPoliza(id, ipc);
        return ResponseEntity.ok(renovada);
    }

    // 4. POST /polizas/{id}/cancelar - Cancelar póliza (y todos sus riesgos)
    @PostMapping("/polizas/{id}/cancelar")
    public ResponseEntity<Poliza> cancelarPoliza(@PathVariable Long id) {
        Poliza cancelada = polizaService.cancelarPoliza(id);
        return ResponseEntity.ok(cancelada);
    }

    // 5. POST /polizas/{id}/riesgos - Agregar riesgo a póliza colectiva
    @PostMapping("/polizas/{id}/riesgos")
    public ResponseEntity<Riesgo> addRiesgo(
            @PathVariable Long id,
            @Valid @RequestBody Riesgo riesgo) {
        Riesgo nuevoRiesgo = polizaService.addRiesgo(id, riesgo);
        return new ResponseEntity<>(nuevoRiesgo, HttpStatus.CREATED);
    }

    // 6. POST /riesgos/{id}/cancelar - Cancelar riesgo individual
    @PostMapping("/riesgos/{id}/cancelar")
    public ResponseEntity<Riesgo> cancelarRiesgo(@PathVariable Long id) {
        Riesgo riesgoCancelado = polizaService.cancelarRiesgo(id);
        return ResponseEntity.ok(riesgoCancelado);
    }
}

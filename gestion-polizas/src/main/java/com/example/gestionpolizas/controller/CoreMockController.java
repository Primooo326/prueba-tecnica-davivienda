package com.example.gestionpolizas.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/core-mock")
public class CoreMockController {

    private static final Logger log = LoggerFactory.getLogger(CoreMockController.class);

    @PostMapping("/evento")
    public ResponseEntity<Map<String, Object>> registrarEvento(@RequestBody Map<String, Object> payload) {
        log.info("[MOCK CORE] Recibido evento de integración en el CORE: {}", payload);
        return ResponseEntity.ok(Map.of(
            "status", "PROCESADO",
            "mensaje", "Evento de integración registrado exitosamente en el CORE legado",
            "detalles", payload
        ));
    }
}

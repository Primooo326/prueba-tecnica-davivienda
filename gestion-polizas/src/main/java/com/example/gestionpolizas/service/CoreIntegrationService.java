package com.example.gestionpolizas.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.Map;

@Service
public class CoreIntegrationService {

    private static final Logger log = LoggerFactory.getLogger(CoreIntegrationService.class);

    @Value("${app.core-mock.url:http://localhost:8080/core-mock/evento}")
    private String coreMockUrl;

    public void notifyCoreOfEvent(String eventType, Long polizaId) {
        log.info("Intentando enviar evento '{}' al CORE para póliza ID: {}", eventType, polizaId);
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", "123456");

            Map<String, Object> payload = new HashMap<>();
            payload.put("evento", eventType);
            payload.put("polizaId", polizaId);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(coreMockUrl, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("Notificación al CORE completada con éxito. Respuesta: {}", response.getBody());
            } else {
                log.warn("El CORE devolvió código de error: {}", response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Resiliencia: No se pudo conectar con el CORE legado, pero la transacción local se completó. Detalle: {}", e.getMessage());
        }
    }
}

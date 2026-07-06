package com.example.gestionpolizas.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    private static final String API_KEY_HEADER = "x-api-key";
    private static final String API_KEY_VALUE = "123456";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        // Permitir H2 console para pruebas locales
        if (path.startsWith("/h2-console")) {
            return true;
        }

        String apiKey = request.getHeader(API_KEY_HEADER);
        if (API_KEY_VALUE.equals(apiKey)) {
            return true;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\": \"No autorizado. x-api-key inválido o faltante.\"}");
        return false;
    }
}

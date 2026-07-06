# API de Gestión de Pólizas (Spring Boot)

Este proyecto es una implementación de la API de Gestión de Pólizas de Arrendamiento para la prueba técnica de Desarrollador TI / Sénior.

## Tecnologías Utilizadas

- **Java 17** (o superior)
- **Spring Boot 3.3.x**
- **Spring Data JPA**
- **H2 Database** (Base de datos en memoria)
- **Gradle 9.5** (Wrapper incluido)

---

## Requisitos de Seguridad

Todos los endpoints (excepto la consola H2) requieren el siguiente header de seguridad obligatorio:
- **Header**: `x-api-key`
- **Valor**: `123456`

---

## Cómo Ejecutar el Proyecto

### 1. Iniciar la aplicación:
En la raíz del proyecto (`d:\prueba-tecnica\gestion-polizas`), ejecute el siguiente comando para levantar el servidor en el puerto `8080`:

**En Windows (PowerShell/CMD):**
```bash
./gradlew.bat bootRun
```

**En Linux / macOS:**
```bash
./gradlew bootRun
```

La aplicación estará disponible en `http://localhost:8080`.

### 2. Consola de Base de Datos H2:
Para depurar y examinar las tablas creadas en memoria, puede ingresar a la consola de H2:
- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:polizasdb`
- **User Name**: `sa`
- **Password**: *(dejar en blanco)*

---

## Endpoints de la API

### 1. Crear Póliza (Auxiliar para Pruebas)
Crea una nueva póliza (individual o colectiva). Calcula automáticamente el valor de la prima (`canonMensual * meses de vigencia`) y establece el estado inicial a `ACTIVA`. Valida que las individuales tengan como máximo 1 riesgo.

- **URL**: `POST /polizas`
- **Headers**: `x-api-key: 123456`, `Content-Type: application/json`
- **Cuerpo (Individual)**:
```json
{
  "tipo": "INDIVIDUAL",
  "fechaInicio": "2026-07-01",
  "fechaFin": "2027-07-01",
  "canonMensual": 1200000.00,
  "tomador": "Juan Perez",
  "asegurado": "Juan Perez",
  "beneficiario": "Inmobiliaria S.A.S",
  "riesgos": [
    {
      "direccionInmueble": "Calle 45 #12-34 apto 402",
      "descripcionRiesgo": "Apartamento residencial"
    }
  ]
}
```
- **Ejemplo `curl`**:
```bash
curl -X POST http://localhost:8080/polizas \
  -H "x-api-key: 123456" \
  -H "Content-Type: application/json" \
  -d '{"tipo":"INDIVIDUAL","fechaInicio":"2026-07-01","fechaFin":"2027-07-01","canonMensual":1200000,"tomador":"Juan Perez","asegurado":"Juan Perez","beneficiario":"Inmobiliaria S.A.S","riesgos":[{"direccionInmueble":"Calle 45 #12-34 apto 402","descripcionRiesgo":"Apartamento"}]}'
```

---

### 2. Listar Pólizas
Devuelve una lista de pólizas, filtrada opcionalmente por tipo y estado.

- **URL**: `GET /polizas?tipo={INDIVIDUAL|COLECTIVA}&estado={ACTIVA|RENOVADA|CANCELADA}`
- **Headers**: `x-api-key: 123456`
- **Ejemplo `curl`**:
```bash
curl -X GET "http://localhost:8080/polizas?tipo=INDIVIDUAL&estado=ACTIVA" \
  -H "x-api-key: 123456"
```

---

### 3. Listar Riesgos de una Póliza
Devuelve todos los riesgos asociados a una póliza específica.

- **URL**: `GET /polizas/{id}/riesgos`
- **Headers**: `x-api-key: 123456`
- **Ejemplo `curl`**:
```bash
curl -X GET http://localhost:8080/polizas/1/riesgos \
  -H "x-api-key: 123456"
```

---

### 4. Renovar Póliza
Incrementa el canon mensual y la prima en base al IPC proporcionado (e.g. `5.0`). El estado pasa a `RENOVADA` y las fechas de vigencia se extienden por el mismo periodo inicial. Envía evento `ACTUALIZACION` al CORE mock.

- **URL**: `POST /polizas/{id}/renovar?ipc={tasa}`
- **Headers**: `x-api-key: 123456`
- **Ejemplo `curl`**:
```bash
curl -X POST "http://localhost:8080/polizas/1/renovar?ipc=5.5" \
  -H "x-api-key: 123456"
```

---

### 5. Cancelar Póliza
Establece el estado de la póliza en `CANCELADA` y cancela automáticamente todos sus riesgos asociados (estado `CANCELADO`). Envía evento `ACTUALIZACION` al CORE mock.

- **URL**: `POST /polizas/{id}/cancelar`
- **Headers**: `x-api-key: 123456`
- **Ejemplo `curl`**:
```bash
curl -X POST http://localhost:8080/polizas/1/cancelar \
  -H "x-api-key: 123456"
```

---

### 6. Agregar Riesgo a Póliza Colectiva
Agrega un nuevo riesgo a una póliza, validando que el tipo de póliza sea `COLECTIVA`. Envía evento `ACTUALIZACION` al CORE mock.

- **URL**: `POST /polizas/{id}/riesgos`
- **Headers**: `x-api-key: 123456`, `Content-Type: application/json`
- **Cuerpo**:
```json
{
  "direccionInmueble": "Avenida Siempre Viva 742",
  "descripcionRiesgo": "Local comercial comercializador"
}
```
- **Ejemplo `curl`**:
```bash
curl -X POST http://localhost:8080/polizas/1/riesgos \
  -H "x-api-key: 123456" \
  -H "Content-Type: application/json" \
  -d '{"direccionInmueble":"Avenida Siempre Viva 742","descripcionRiesgo":"Local comercial"}'
```

---

### 7. Cancelar Riesgo Individual
Establece el estado del riesgo en `CANCELADO`. Envía evento `ACTUALIZACION` de la póliza dueña al CORE mock.

- **URL**: `POST /riesgos/{id}/cancelar`
- **Headers**: `x-api-key: 123456`
- **Ejemplo `curl`**:
```bash
curl -X POST http://localhost:8080/riesgos/1/cancelar \
  -H "x-api-key: 123456"
```

---

### 8. Endpoint Mock del CORE Legado
Simula el endpoint de la capa media en WebLogic. Recibe la notificación de creación/actualización de pólizas y registra la traza en los logs.

- **URL**: `POST /core-mock/evento`
- **Headers**: `x-api-key: 123456`, `Content-Type: application/json`
- **Cuerpo**:
```json
{
  "evento": "ACTUALIZACION",
  "polizaId": 1
}
```

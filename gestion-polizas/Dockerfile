# Etapa 1: Compilación de la aplicación
FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

# Copiar archivos de construcción necesarios (sin copiar gradle.properties para evitar conflictos de rutas locales)
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

# Dar permisos de ejecución y compilar omitiendo los tests (los tests ya se validaron localmente)
RUN chmod +x gradlew
RUN ./gradlew bootJar --no-daemon -x test

# Etapa 2: Imagen de ejecución (Runtime)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

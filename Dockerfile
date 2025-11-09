# Etapa de build
FROM maven:3.9.6-eclipse-temurin-17-alpine AS build
WORKDIR /workspace
COPY pom.xml .
# Descarga dependencias (caché)
RUN mvn -q -DskipTests dependency:go-offline
COPY src src
RUN mvn -q -DskipTests clean package

# Etapa runtime ligera
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
ENV JAVA_OPTS=""
# Copia jar construido
COPY --from=build /workspace/target/zugarez-BACK-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8000
# Health (opcional en Cloud Run via /actuator/health)
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
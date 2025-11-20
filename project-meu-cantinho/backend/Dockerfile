# Estágio 1: Build (Compilação)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Compila o projeto e gera o .jar
RUN mvn clean package -DskipTests

# Estágio 2: Runtime (Execução Leve)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copia apenas o JAR gerado no estágio anterior
COPY --from=build /app/target/*.jar app.jar

# Expõe a porta padrão do Spring
EXPOSE 8080

# Variáveis de ambiente padrão (podem ser sobrescritas pelo docker-compose)
ENV DB_URL=jdbc:postgresql://db:5432/seucantinho
ENV DB_USER=postgres
ENV DB_PASS=postgres

ENTRYPOINT ["java", "-jar", "app.jar"]
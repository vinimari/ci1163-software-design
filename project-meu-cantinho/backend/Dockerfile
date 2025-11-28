# Estágio 1: Build (Compilação)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .

# Baixa as dependências
RUN mvn dependency:go-offline

# Copia o código fonte
COPY src ./src

# Compila o projeto
RUN mvn clean package -Dmaven.test.skip=true

# Estágio 2: Runtime (Execução Leve)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Cria um usuário não-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/target/*.jar app.jar

# Expõe a porta
EXPOSE 8080

# Variáveis de ambiente padrão
ENV DB_URL=jdbc:postgresql://db:5432/seucantinho
ENV DB_USER=postgres
ENV DB_PASS=postgres

ENTRYPOINT ["java", "-jar", "app.jar"]
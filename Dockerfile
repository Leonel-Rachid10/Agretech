# Estágio 1: Construção (Build)
FROM maven:3.9.6-eclipse-temurin-17-focal AS build
WORKDIR /app
COPY backend/pom.xml ./pom.xml
COPY backend/src ./src
RUN mvn clean package -DskipTests

# Estágio 2: Execução (Run)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
RUN mkdir -p /app/data
COPY --from=build /app/target/agritech-dondo-1.0.0.jar app.jar

# Fallback para banco de dados H2 interno caso não seja fornecido MySQL externo (100% gratuito, sem cartão)
ENV SPRING_DATASOURCE_URL="jdbc:h2:file:/app/data/agritech;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DEFAULT_NULL_ORDER=HIGH;DB_CLOSE_DELAY=-1"
ENV SPRING_DATASOURCE_USERNAME="sa"
ENV SPRING_DATASOURCE_PASSWORD=""

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

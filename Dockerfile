# syntax=docker/dockerfile:1
#
# Dockerfile multi-stage:
# - Primeiro stage compila o JAR com Maven Wrapper.
# - Segundo stage executa a aplicacao em uma imagem menor (JRE).
#
# Observacao: os testes sao ignorados no build do container para nao exigir
# infraestrutura (ex.: banco) durante a construcao da imagem.

FROM eclipse-temurin:21-jdk AS build

WORKDIR /workspace

# Copiamos primeiro arquivos de build para aproveitar cache de dependencias.
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -q -DskipTests dependency:go-offline

# Copiamos o codigo fonte somente depois do cache de dependencias.
COPY src/ src/

# Gera o artefato executavel do Spring Boot.
RUN ./mvnw -q -DskipTests package


FROM eclipse-temurin:21-jre

WORKDIR /app

# Copia o JAR gerado no stage de build.
COPY --from=build /workspace/target/*.jar /app/app.jar

# Porta padrao do Spring Boot.
EXPOSE 8080

# Permite injetar flags da JVM via variavel de ambiente.
ENV JAVA_OPTS=""

# Usa "sh -c" para permitir expansao de JAVA_OPTS.
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

# URL Shortener API

API de encurtamento de URLs (estilo Bitly) desenvolvida como projeto de portfolio backend.

## Descrição do projeto

Este projeto expõe endpoints para:

- Criar uma URL curta a partir de uma URL original.
- Redirecionar a partir de um `shortCode` para a URL original.
- Contabilizar cliques (contador de acessos) a cada redirecionamento.

O foco aqui é qualidade de código, organização em camadas e boas práticas.

## Arquitetura

Arquitetura em camadas (package-by-layer), com responsabilidades claras:

- `controller`: endpoints HTTP (contratos REST).
- `service`: regras de negócio e casos de uso.
- `repository`: acesso a dados com Spring Data JPA.
- `entity`: entidades JPA persistidas.
- `dto`: objetos de entrada/saída da API.
- `exception`: exceções de domínio e handler global.
- `config`: configurações de infraestrutura (quando aplicável).
- `util`: utilitários utilizáveis (sem regra de negócio).

Fluxo típico:

`Controller -> Service -> Repository -> Banco`

## Stack utilizada

- Java 21
- Spring Boot
- Maven
- Spring WebMVC
- Spring Data JPA (Hibernate)
- Bean Validation (Spring Validation)
- PostgreSQL
- Docker / Docker Compose

## Como rodar localmente

### Pré-requisitos

- Java 21
- Maven (ou Maven Wrapper `./mvnw`)
- PostgreSQL rodando localmente

### Variáveis de ambiente

A aplicação se conecta ao banco via variáveis de ambiente. Caso nao informe, existem defaults para desenvolvimento.

- `SPRING_DATASOURCE_URL` (default: `jdbc:postgresql://localhost:5432/url_shortener`)
- `SPRING_DATASOURCE_USERNAME` (default: `postgres`)
- `SPRING_DATASOURCE_PASSWORD` (default: `postgres`)
- `SPRING_JPA_HIBERNATE_DDL_AUTO` (default: `update`)

### Subir a aplicação

1. Crie um banco no Postgres (ex.: `url_shortener`).
2. Export as variáveis (opcional, se estiver usando defaults).
3. Rode:

```bash
./mvnw spring-boot:run
```

A API sobe em `http://localhost:8080`.

### Testes

```bash
./mvnw test
```

Observações:

- Existe um teste de contexto (`@SpringBootTest`) usando perfil `test` com H2 em memória.
- Os testes unitários do service usam JUnit + Mockito.

## Como rodar com Docker

### Subir com Docker Compose

```bash
docker compose up --build
```

O Compose sobe:

- `db` (PostgreSQL 16)
- `api` (Spring Boot)

A API fica disponível em `http://localhost:8080`.

### Parar e remover

```bash
docker compose down
```

Para remover também o volume do Postgres:

```bash
docker compose down -v
```

## Endpoints da API

### POST `/api/shorten`

Cria uma URL curta.

Request:

```json
{
  "url": "https://example.com"
}
```

Response (201):

```json
{
  "shortUrl": "http://localhost:8080/abc123"
}
```

Validações:

- Não aceita body vazio.
- Valida formato de URL via Bean Validation.

Erros (exemplos):

- `400 Bad Request` com JSON padronizado quando a validação falhar.

### GET `/{shortCode}`

Redireciona para a URL original.

- Resposta: `302 Found` com header `Location: <originalUrl>`.
- Incrementa o contador de cliques para o `shortCode`.

Erros:

- `404 Not Found` quando o `shortCode` não existir.
- `410 Gone` quando a URL curta estiver expirada.

## Exemplos de requisicao

### Encurtar uma URL

```bash
curl -i -X POST \
  -H 'Content-Type: application/json' \
  -d '{"url":"https://example.com"}' \
  http://localhost:8080/api/shorten
```

### Redirecionar

Troque `abc123` pelo codigo retornado no passo anterior:

```bash
curl -i http://localhost:8080/abc123
```

## Melhorias futuras

- Usar migrations (Flyway ou Liquibase) em vez de `ddl-auto=update`.
- Criar endpoint de estatisticas (ex.: `GET /api/{shortCode}/stats`) com `clickCount`, datas e expiração.
- Adicionar expiracao no request de criacao e regras (min/max, timezone, etc.).
- Implementar rate limiting (por IP / API key) e protecao contra abuso.
- Validação mais forte de URL (permitir apenas http/https, blacklist/allowlist de hosts, etc.).
- Observabilidade: logs estruturados, tracing, metricas (Micrometer/Prometheus).
- Seguranca: autenticação/autorizacao para gerenciamento de links.

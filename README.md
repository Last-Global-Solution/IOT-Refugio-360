# Refúgio 360 — API & Integração IoT / IA

API REST do **Refúgio 360**, plataforma CitizenTech e IoT desenvolvida para apoiar comunidades em eventos climáticos críticos — como ondas de calor, enchentes e quedas de energia. O serviço centraliza o cadastro de abrigos e locais seguros, recebe ocorrências e alertas monitorados, mapeia áreas de risco e gera orientações personalizadas em tempo real utilizando Inteligência Artificial generativa.

---

## Links do Projeto

| Recurso | Endereço |
|---|---|
| **Swagger UI (Produção)** | https://java-refugio360.onrender.com/swagger-ui/index.html |
| **API Deploy (Render)** | https://java-refugio360.onrender.com |
| **Repositório GitHub** | https://github.com/Last-Global-Solution/IOT-Refugio-360 |

---

## Arquitetura e Responsabilidades

A aplicação combina autenticação híbrida (Firebase + JWT local), mensageria assíncrona com RabbitMQ e geração de recomendações contextualizadas via Spring AI (Google Gemini).

| Camada / Responsabilidade | Tecnologia |
|---|---|
| Cadastro e login no aplicativo | Firebase Authentication |
| Validação de token e controle de acesso (`CIDADAO` / `GESTOR`) | Spring Security + JWT |
| Persistência de locais seguros, ocorrências e pontos de risco | Spring Data JPA + H2 Database |
| Orientações inteligentes por perfil de vulnerabilidade | Spring AI + Google Gemini (`gemini-3.5-flash-lite`) |
| Processamento assíncrono de eventos de ocorrência | RabbitMQ (`OcorrenciaRegistrada`) |

---

## Como Executar Localmente

### Pré-requisitos
* **JDK 21**
* **Docker** e **Docker Compose** (opcional, para mensageria com RabbitMQ)

### Passo a passo

1. **Suba o RabbitMQ via Docker:**
   ```bash
   docker compose up -d
   ```

2. **Inicie a aplicação Spring Boot:**

   **Windows (PowerShell):**
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

   **Linux / macOS:**
   ```bash
   ./mvnw spring-boot:run
   ```

   > **Nota:** Caso o RabbitMQ não esteja em execução, a API inicia normalmente e registra as ocorrências sem interrupções; apenas o disparo assíncrono fica inativo, mantendo disponível a geração sob demanda via `POST /api/ai/ocorrencias/{id}/orientacao`.

   Se o Java 21 não for a versão padrão do sistema no Windows, configure o `JAVA_HOME` na sessão antes de rodar:
   ```powershell
   $env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'
   $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
   .\mvnw.cmd spring-boot:run
   ```

### Endereços Locais

| Serviço | URL | Credenciais / Observações |
|---|---|---|
| **Swagger UI** | http://localhost:8080/swagger-ui/index.html | Documentação interativa |
| **OpenAPI JSON** | http://localhost:8080/v3/api-docs | Especificação da API |
| **Console H2** | http://localhost:8080/h2-console | JDBC: `jdbc:h2:file:./data/refugio360;AUTO_SERVER=TRUE` \| User: `sa` \| Senha: *(vazia)* |
| **RabbitMQ Management** | http://localhost:15672 | User: `guest` \| Senha: `guest` |

---

## Variáveis de Ambiente

A API funciona localmente com valores padrão seguros, mas permite configurar serviços externos via variáveis de ambiente:

| Variável | Descrição | Padrão Local |
|---|---|---|
| `GEMINI_API_KEY` | Chave da API do Google AI Studio (Gemini) | `local-disabled-key` *(usa fallback local)* |
| `GEMINI_MODEL` | Modelo Gemini utilizado pelo Spring AI | `gemini-3.5-flash-lite` |
| `FIREBASE_PROJECT_ID` | ID do projeto Firebase para validação de ID Tokens | *(vazio — desabilitado)* |
| `CORS_ALLOWED_ORIGINS` | Origens permitidas para o front-end | `http://localhost:3000,http://localhost:5173,http://localhost:8081` |
| `DATABASE_URL` | URL JDBC do banco de dados | `jdbc:h2:file:./data/refugio360` |
| `GESTOR_EMAIL` | E-mail do usuário gestor criado no bootstrap | `gestor@refugio360.local` |
| `GESTOR_PASSWORD` | Senha inicial do gestor local | `gestor12345` |
| `JWT_SECRET` | Chave de assinatura dos tokens JWT locais (mín. 32 bytes) | Chave padrão de desenvolvimento |
| `RABBITMQ_HOST` / `RABBITMQ_PORT` | Host e porta do broker RabbitMQ | `localhost` / `5672` |
| `RABBITMQ_USERNAME` / `RABBITMQ_PASSWORD` | Credenciais de acesso ao RabbitMQ | `guest` / `guest` |

Exemplo de configuração no PowerShell antes de iniciar:
```powershell
$env:GEMINI_API_KEY = "sua-chave-google-ai-studio"
$env:FIREBASE_PROJECT_ID = "seu-projeto-firebase"
.\mvnw.cmd spring-boot:run
```

---

## Autenticação e Perfis de Acesso

### 1. Fluxo Firebase (Aplicativo Mobile / Front-end)
1. O usuário autentica-se via Firebase Authentication e obtém o token com `user.getIdToken()`.
2. No primeiro acesso, o cliente chama `POST /api/auth/firebase` enviando `Authorization: Bearer <idToken>`. A API vincula ou cria o usuário localmente com papel `CIDADAO` (ou `GESTOR`, se o e-mail verificado coincidir com `GESTOR_EMAIL`).
3. Nas requisições seguintes, basta enviar o mesmo header `Authorization: Bearer <idToken>`.

### 2. Fluxo JWT Local (Insomnia / Swagger / Testes)
* **Cadastro (`POST /api/auth/register`):**
  ```json
  {
    "email": "cidadao@refugio360.local",
    "senha": "senha-segura-123"
  }
  ```
* **Login (`POST /api/auth/login`):**
  ```json
  {
    "email": "gestor@refugio360.local",
    "senha": "gestor12345"
  }
  ```
  Retorna `accessToken` com validade de **1 hora**. No Swagger UI, clique em **Authorize** e cole apenas o valor do token.

### Matriz de Permissões

| Funcionalidade | Perfil Necessário |
|---|---|
| Consultar locais seguros e pontos de risco | Público |
| Cadastrar, editar e remover locais seguros e pontos de risco | `GESTOR` |
| Registrar e listar ocorrências | `CIDADAO` ou `GESTOR` |
| Alterar status, editar ou excluir ocorrências | `GESTOR` |
| Visualizar ou solicitar orientação de IA para uma ocorrência | Autor da ocorrência ou `GESTOR` |

---

## Endpoints Principais

Todas as listagens (`GET`) suportam paginação e ordenação via Spring Data (`?page=0&size=10&sort=campo,asc`).

| Domínio | Método e Rota | Acesso |
|---|---|---|
| **Auth** | `POST /api/auth/register` \| `POST /api/auth/login` | Público |
| **Auth** | `POST /api/auth/firebase` \| `GET /api/auth/me` | Autenticado |
| **Locais Seguros** | `GET /api/locais-seguros` \| `GET /api/locais-seguros/{id}` | Público |
| **Locais Seguros** | `POST` \| `PUT` \| `DELETE /api/locais-seguros/{id}` | `GESTOR` |
| **Ocorrências** | `GET /api/ocorrencias` \| `GET /api/ocorrencias/{id}` \| `POST /api/ocorrencias` | Autenticado |
| **Ocorrências** | `GET /api/ocorrencias/minhas` | Autenticado (próprias) |
| **Ocorrências** | `GET /api/ocorrencias/{id}/orientacoes` | Autor ou `GESTOR` |
| **Ocorrências** | `PUT /api/ocorrencias/{id}` \| `PATCH /api/ocorrencias/{id}/status` \| `DELETE /api/ocorrencias/{id}` | `GESTOR` |
| **Pontos de Risco** | `GET /api/pontos-risco` \| `GET /api/pontos-risco/{id}` | Público |
| **Pontos de Risco** | `POST` \| `PUT` \| `DELETE /api/pontos-risco/{id}` | `GESTOR` |
| **Inteligência Artificial** | `POST /api/ai/ocorrencias/{id}/orientacao?perfil=IDOSO` | Autor ou `GESTOR` |

### Exemplos de Payload

**Registrar Ocorrência (`POST /api/ocorrencias`):**
```json
{
  "descricao": "Alagamento severo próximo à avenida principal com risco de isolamento",
  "urgencia": "CRITICA",
  "latitude": -23.5841,
  "longitude": -46.5829,
  "perfil": "IDOSO"
}
```
* **Urgências aceitas:** `BAIXA`, `MEDIA`, `ALTA`, `CRITICA`
* **Perfis de orientação:** `GERAL`, `IDOSO`, `PCD`, `FAMILIA`
* **Ciclo de status:** `PENDENTE` → `EM_ANALISE` → `RESOLVIDA`

**Cadastrar Local Seguro (`POST /api/locais-seguros`):**
```json
{
  "nome": "UBS Vila Prudente - Ponto de Apoio",
  "tipo": "ABRIGO",
  "endereco": "Av. Professor Luiz Ignácio Anhaia Mello, 1200",
  "latitude": -23.5845,
  "longitude": -46.5832,
  "descricao": "Ponto de hidratação, energia de emergência e acesso para cadeirantes"
}
```

---

## Fluxo Assíncrono (RabbitMQ + Spring AI)

```text
App / Sensor ──POST /api/ocorrencias──▶ API salva no H2 ──(após commit)──▶ RabbitMQ
                                                                              │ exchange: refugio360.eventos
                                                                              │ fila: refugio360.ocorrencias.orientacao
                                                                              ▼
                                             OrientacaoListener ──▶ Gemini AI ──▶ Persiste Orientacao
```

1. Ao receber uma ocorrência, a API grava os dados e publica o evento `OcorrenciaRegistrada` na exchange `refugio360.eventos`.
2. O consumidor `OrientacaoListener` cruza a localização da ocorrência com os **3 locais seguros mais próximos** e aciona o Spring AI (`gemini-3.5-flash-lite`) adaptado ao `perfil` informado.
3. O resultado é salvo com `"geradoPorIA": true` e os IDs em `locaisConsiderados`. Em caso de indisponibilidade externa, o sistema garante continuidade operacional com orientação estruturada local (`"geradoPorIA": false`).

---

## Estrutura do Projeto

```text
src/main/java/org/fiap/gs_final/
├── ai/          # Integração com Spring AI, prompts e fallback de orientações
├── config/      # Configurações de OpenAPI/Swagger, paginação e cliente HTTP (HttpClientConfig)
├── controller/  # Controladores REST e documentação de rotas
├── dto/         # Objetos de transferência e validações Bean Validation
├── exception/   # Tratamento global de exceções e respostas padronizadas
├── mensageria/  # Configuração de filas, producer e consumer RabbitMQ
├── model/       # Entidades JPA (Usuario, Ocorrencia, Orientacao, LocalSeguro, PontoRisco)
├── repository/  # Interfaces Spring Data JPA
├── security/    # Filtros JWT, validação Firebase, CORS e regras de acesso
└── service/     # Regras de negócio e cálculos de proximidade
```

---

## Equipe

* **Gabriely Bonfim Silva** — RM566242
* **Henrique Rodrigues Vespasiano** — RM562917
* **Mirelly Sousa Alves** — RM566299
* **Ruan Luca Feliciano** — RM562218
* **Murilo Macedo Silva** — RM566462

---

## Tecnologias Utilizadas

Java 21 · Spring Boot 3.4 · Spring Web · Spring Security · Spring Data JPA · H2 Database · Spring AI · Google Gemini · RabbitMQ (Spring AMQP) · Docker Compose · Firebase Authentication · Swagger / OpenAPI 3 · Maven

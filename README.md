# API de Gestao de Riscos - SETIC Hackathon 2026

Sistema de gestao de riscos baseado no Modelo de Gestao de Riscos (MGR), implementando o fluxo completo desde a identificacao ate a avaliacao de Planos de Resposta a Riscos (PRR).

## Tecnologias

- **Java 21**
- **Spring Boot 4.0.9** (Spring Security, Spring Data JPA, Web)
- **PostgreSQL 18.6**
- **JWT RS256** (autenticacao com chaves RSA)
- **Maven**

---

## Pré-requisitos para Rodar em Outra Máquina

Antes de iniciar, certifique-se de ter instalado na máquina de destino:

1. **JDK 27** (ou versão compatível configurada no `JAVA_HOME`).
2. **Maven 3.8+** (ou o wrapper `./mvnw` do projeto).
3. **PostgreSQL 16+** com o serviço ativo na porta `5432`.
4. **OpenSSL** (para geração do par de chaves RSA).

---

## Passo a Passo para Configuração

### 1. Clonar o Repositório
```bash
git clone <URL_DO_REPOSITORIO>
cd hackathon-setic-2026
```

---

### 2. Configurar o Banco de Dados (PostgreSQL)

1. Crie o banco de dados `postgres` (ou utilize o banco padrão da sua instalação local).
2. Execute o arquivo de dump SQL (`schema_hackaton.sql`) no seu PostgreSQL (via **pgAdmin** ou CLI):

```bash
psql -U postgres -d postgres -f schema_hackaton.sql
```

3. **Inserção do Usuário Inicial (MANDATÓRIO):**
   Como as tabelas estarão sem dados por padrão, execute o seguinte script no PostgreSQL para criar o setor, cargo e um usuário válido para autenticação no sistema:

```sql
-- 1. Criação do Setor inicial
INSERT INTO public.setor (id_setor, unidades, sigla, unidade) 
VALUES (1, 'Coordenadoria de Teste', 'Cote', 'SETIC')
ON CONFLICT (id_setor) DO NOTHING;

-- 2. Criação do Cargo inicial
INSERT INTO public.cargo (id_cargo, cargo, descricao) 
VALUES (1, 'Cadastrador', 'Responsável por cadastrar e gerenciar riscos')
ON CONFLICT (id_cargo) DO NOTHING;

-- 3. Criação do Usuário padrão para login
INSERT INTO public.usuario (id_usuario, nome, email, senha, id_setor, id_cargo, ativo) 
VALUES (1, 'Adm da Silva', 'emaildeadm@setic.ro.gov.br', 'senha123', 1, 1, true)
ON CONFLICT (id_usuario) DO NOTHING;
```

---

### 3. Configurar a Pasta `src/main/resources` e Credenciais


1. Crie o diretório `src/main/resources` no projeto, se ele não existir.
2. Crie o arquivo `src/main/resources/application.properties` com o seguinte conteúdo:

```properties
spring.application.name=hackathon-setic-2026
server.port=8080

# Configuração do PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=SUA_SENHA_AQUI
spring.datasource.driver-class-name=org.postgresql.Driver

# Configuração do JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Inicialização do Script SQL (opcional)
spring.sql.init.mode=always
```

---

### 4. Gerar as Chaves RSA256 de Segurança

A autenticação do sistema utiliza chaves RSA256. É obrigatório ter as chaves **`private_key.pem`** e **`public_key.pem`** na **raiz do projeto** (`/hackathon-setic-2026/`).

Gere as chaves executando os seguintes comandos no terminal, dentro da pasta raiz do projeto:

```bash
# 1. Gera a chave privada RSA (2048 bits)
openssl genpkey -algorithm RSA -out private_key.pem -pkeyopt rsa_keygen_bits:2048

# 2. Extrai a chave pública correspondente
openssl rsa -pubout -in private_key.pem -out public_key.pem
```

---

### 5. Configurar os Arquivos Estáticos (Frontend)

Para que a interface web seja servida diretamente pelo servidor Spring Boot:

1. Crie a pasta `src/main/resources/static/`.
2. Certifique-se de que os arquivos da interface estejam nessa pasta:
   - `src/main/resources/static/login.html` (Tela de autenticação) - REQUISITAR AO PEDRO PRA ENVIAR, ELE FICOU COM PREGUIÇA DE AJUSTAR DE IMEDIATO
   - `src/main/resources/static/index.html` (Dashboard do MGR) - IDEM DE CIMA

---

## Como Executar o Projeto

### Via Terminal (Maven)
```bash
mvn clean package
java -jar target/hackathon-setic-2026-0.0.1-SNAPSHOT.jar
```

### Via IntelliJ IDEA
1. Abra a pasta do projeto no IntelliJ IDEA.
2. Aguarde a importação das dependências do Maven.
3. Abra a classe principal: `br.com.hackathon.setic.HackathonSetic2026Application.java`.
4. Clique no ícone de **Play** (ou pressione `Shift + F10`).

---

## Como Acessar a Aplicação

Com o backend em execução na porta `8080`:

1. Abra o navegador e acesse: **`http://localhost:8080/login.html`**
2. **Credenciais Padrão de Teste:**
   - **E-mail:** `emaildeadm@setic.ro.gov.br`
   - **Senha:** `senha123`
3. Após o login bem-sucedido, o sistema armazenará a sessão e redirecionará automaticamente para o painel principal (`http://localhost:8080/index.html`).

---

## Arquitetura

### Camadas da Aplicacao

```
src/main/java/br/com/hackathon/setic/
├── config/          # Configuracoes (Seguranca, JWT, CORS)
├── controller/      # Endpoints REST
├── dto/            # Data Transfer Objects (Request/Response)
├── model/          # Entidades JPA
├── repository/     # Repositories Spring Data JPA
└── service/        # Logica de negocio
```

### Fluxo do Sistema

1. **Identificacao de Risco** - Gestor de Risco registra um risco
2. **Estabelecimento do Contexto** - Define escopo e roadmap de etapas
3. **Plano de Resposta ao Risco (PRR)** - Responsavel PRR cria o plano de tratamento
4. **Avaliacao CSGR** - Comite avalia e aprova/reprova o PRR
5. **Monitoramento** - Historico completo de todas as operacoes

### Entidades Principais

- **Risco**: Evento identificado com causa, evento, impacto e nivel
- **Contexto**: Estabelecimento do contexto do risco
- **ContextoEtapa**: Roadmap de etapas do contexto
- **PlanoResposta (PRR)**: Plano de resposta ao risco
- **ParecerPrr**: Avaliacao do CSGR sobre o PRR
- **TratamentoRiscos**: Tipo de tratamento (evitar, transferir, mitigar, aceitar)
- **Parametros**: Parametros de medicao do PRR
- **Historico**: Auditoria de todas as operacoes

---

## Autenticacao

A API utiliza JWT RS256 com chaves RSA. Todas as rotas (exceto `/api/v1/login`) requerem autenticacao.

### Roles/Permissoes

- `SCOPE_GESTOR_RISCO`: Gerencia riscos e contextos
- `SCOPE_RESPONSAVEL_PRR`: Cria e atualiza PRRs
- `SCOPE_CSGR`: Avalia e aprova/reprova PRRs
- `SCOPE_CGGE`: Visualizacao de todos os dados

---

## Principais Endpoints da API

### Autenticacao

#### `POST /api/v1/login`
Login do usuario

**Request:**
```json
{
  "email": "usuario@email.com",
  "senha": "senha123"
}
```

---

### Riscos

#### `POST /api/v1/risco`
Cria um novo risco - **Permissao:** `SCOPE_GESTOR_RISCO`

**Request:**
```json
{
  "idCategoria": 1,
  "causa": "Falta de capacitacao",
  "evento": "erro na execucao",
  "impactoTxt": "atrasos no projeto",
  "objetivo": "conclusao no prazo",
  "idProbabilidade": 3,
  "idImpacto": 4,
  "horizonteMeses": 6
}
```

#### `GET /api/v1/risco/setor`
Lista riscos do setor - **Permissao:** `SCOPE_GESTOR_RISCO`

#### `GET /api/v1/risco/todos`
Lista todos os riscos - **Permissao:** `SCOPE_CGGE`

#### `GET /api/v1/risco/{id}`
Busca risco por ID - **Permissao:** `SCOPE_GESTOR_RISCO`, `SCOPE_CGGE`

---

### Contexto

#### `POST /api/v1/contexto`
Cria o contexto de um risco - **Permissao:** `SCOPE_GESTOR_RISCO`

**Request:**
```json
{
  "idRisco": 1,
  "escopo": "Implementacao de treinamento",
  "etapas": [
    {
      "etapa": "Levantamento de necessidades",
      "prazo": "2026-12-01"
    }
  ]
}
```

#### `GET /api/v1/contexto/risco/{idRisco}`
Busca contexto de um risco - **Permissao:** `SCOPE_GESTOR_RISCO`, `SCOPE_RESPONSAVEL_PRR`

#### `PUT /api/v1/contexto/etapa/{idEtapa}/concluir`
Marca etapa como concluida - **Permissao:** `SCOPE_GESTOR_RISCO`

---

### Plano de Resposta ao Risco (PRR)

#### `POST /api/v1/prr`
Cria um Plano de Resposta - **Permissao:** `SCOPE_RESPONSAVEL_PRR`

**Request:**
```json
{
  "idContexto": 1,
  "idTratamento": 3,
  "idParametro": 2,
  "nomeResponsaveis": "João Silva, Maria Santos",
  "controlesExistentes": "Revisao de codigo, testes",
  "acaoProposta": "Programa de treinamento",
  "metricas": "Reducao de 50% nos erros"
}
```

#### `GET /api/v1/prr/contexto/{idContexto}`
Busca PRR de um contexto - **Permissao:** `SCOPE_RESPONSAVEL_PRR`, `SCOPE_CSGR`

#### `PUT /api/v1/prr/{id}`
Atualiza um PRR - **Permissao:** `SCOPE_RESPONSAVEL_PRR`

---

### Parecer CSGR

#### `POST /api/v1/parecer`
CSGR avalia um PRR - **Permissao:** `SCOPE_CSGR`

**Request:**
```json
{
  "idResposta": 1,
  "statusResultado": "APROVADO",
  "parecerTexto": "PRR aprovado"
}
```

#### `GET /api/v1/parecer/prr/{idResposta}`
Lista pareceres de um PRR - **Permissao:** `SCOPE_GESTOR_RISCO`, `SCOPE_CSGR`, `SCOPE_CGGE`

---

## Status do PRR

- `PENDENTE`: Aguardando avaliacao
- `APROVADO`: Aprovado pelo CSGR
- `REPROVADO`: Reprovado pelo CSGR

---

## Auditoria

Todas as operacoes sao registradas na tabela `historico` com usuario, data/hora, operacao e valores antes/depois (JSON).

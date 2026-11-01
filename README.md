# MGR - Sistema de Gestão de Riscos (SETIC - Hackathon 2026)

Este projeto é um sistema web para **Gestão de Riscos Institucionais (MGR)** desenvolvido para a **SETIC (Superintendência Estadual de Tecnologia da Informação e Comunicação)** durante o Hackathon 2026. A aplicação combina um backend robusto em **Java 27 / Spring Boot** com persistência em **PostgreSQL**, autenticação via **JWT (RSA256)** e um frontend estático integrado.

---

## Tecnologias Utilizadas

- **Java 27** (OpenJDK)
- **Spring Boot 4.0.9** (Spring Security, Spring Data JPA, Web)
- **PostgreSQL 16+**
- **JWT (JSON Web Token)** com par de chaves **RSA256**
- **HTML5, Tailwind CSS, Lucide Icons e JavaScript (Fetch API)**

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

## Principais Endpoints da API

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/auth/login` | Realiza autenticação e valida credenciais na tabela `usuario` |
| `GET` | `/api/usuarios` | Lista todos os usuários cadastrados no banco |
| `POST` | `/api/usuarios` | Cria um novo registro de usuário |

---

## Solução de Problemas Frequentes

- **Erro `Illegal base64 character` no Spring:**
  Verifique se o arquivo `RsaKeyService.java` está tratando a remoção de cabeçalhos das chaves pública e privada (`-----BEGIN...-----` e `-----END...-----`).
- **Erro de Conexão com o PostgreSQL:**
  Verifique se o usuário, senha e porta no `application.properties` correspondem à sua instância local do PostgreSQL.
- **Erro de Chave Estrangeira `fk_cargo_usuario`:**
  Garantir que a coluna `id_cargo` na tabela `usuario` seja do tipo `bigint` e não `varchar`.

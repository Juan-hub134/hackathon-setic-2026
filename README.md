<<<<<<< Updated upstream
# MGR - Sistema de Gestão de Riscos (SETIC - Hackathon 2026)

Este projeto é um sistema web para **Gestão de Riscos Institucionais (MGR)** desenvolvido para a **SETIC (Superintendência Estadual de Tecnologia da Informação e Comunicação)** durante o Hackathon 2026. A aplicação combina um backend robusto em **Java 27 / Spring Boot** com persistência em **PostgreSQL**, autenticação via **JWT (RSA256)** e um frontend estático integrado.

---

## Tecnologias Utilizadas

- **Java 27** (OpenJDK)
- **Spring Boot 4.0.9** (Spring Security, Spring Data JPA, Web)
- **PostgreSQL 16+**
- **JWT (JSON Web Token)** com par de chaves **RSA256**
- **HTML5, Tailwind CSS, Lucide Icons e JavaScript (Fetch API)**
=======
# API de Gestão de Riscos - SETIC Hackathon 2026

Aplicação de gestão de riscos baseada no Modelo de Gestão de Riscos (MGR), com
interface web servida pelo Spring Boot e persistência em PostgreSQL.

## Requisitos

- JDK 21 ou superior.
- PostgreSQL 16 ou superior em execução.
- OpenSSL para gerar as chaves JWT locais.
- Acesso à internet na primeira execução do Maven Wrapper (`./mvnw`).
>>>>>>> Stashed changes

## Executar localmente

### 1. Preparar o banco

O backend valida o esquema existente (`ddl-auto=validate`) e não cria ou altera
tabelas automaticamente. A inicialização de scripts SQL também está desativada.

Para criar um banco local separado e restaurar nele o backup SQL:

```bash
export PGUSER=postgres
export PGPASSWORD='senha_do_postgres'
./scripts/restore-local-db.sh "/caminho/para/backup-pedro-plain.sql"
```

O script cria somente um banco novo chamado `hackathon_setic_local` (ou o nome
opcional informado como segundo argumento), recusa sobrescrever banco existente
e não modifica o backup. Durante a importação, remove apenas diretivas de dump
gerado pelo PostgreSQL 18 incompatíveis com PostgreSQL 16. Use uma conta com
permissão para criar banco e, no restore, para definir o proprietário `postgres`.

Se preferir usar um banco que já existe no pgAdmin, pule a restauração e configure
`DB_URL` para apontar para ele. O banco deve conter o esquema compatível com as
entidades da aplicação.

Para habilitar as solicitações diretas do titular à CSGR, aplique a migração
aditiva antes de iniciar a aplicação (o Hibernate valida o esquema, mas não cria
tabelas):

```bash
psql -h localhost -U postgres -d hackathon_setic_local -f scripts/migration-add-csgr-solicitacoes.sql
```

Troque o nome do banco se `DB_URL` apontar para outro banco. A migração cria
somente a tabela de solicitações e seu índice de pendências; ela não altera riscos,
PRRs ou pareceres existentes.

Para popular o banco configurado com cenários fictícios de teste do MGR:

```bash
python3 scripts/seed-mgr-test-data.py --dry-run
python3 scripts/seed-mgr-test-data.py
```

A carga cria riscos, contextos, etapas de roadmap, PRRs e pareceres marcados
`TESTE_MGR`, seguindo o modelo causa–evento–impacto–objetivo e o cálculo
probabilidade × impacto. Ela é aditiva e idempotente: não atualiza nem exclui
registros existentes; pode ser executada novamente sem duplicar os cenários.
Confira `DB_URL` no `.env` antes de executar.

### 2. Configurar a conexão PostgreSQL

Informe a senha por variável de ambiente; não a grave no código:

```bash
export DB_USERNAME=postgres
export DB_PASSWORD='senha_do_postgres'
# Opcional se usar um banco diferente:
export DB_URL='jdbc:postgresql://localhost:5432/nome_do_banco'
```

No PowerShell, defina `$env:DB_USERNAME`, `$env:DB_PASSWORD` e, se necessário,
`$env:DB_URL`. O valor padrão de `DB_URL` aponta para `hackathon_setic_local`.
A porta HTTP pode ser alterada com `PORT` (padrão `8080`).

Para iniciar pelo botão **Run** do IntelliJ, crie um arquivo `.env` na raiz do
repositório (não uma pasta chamada `.env`), copiando `.env.example`, e substitua
`DB_PASSWORD` pela senha usada no pgAdmin.
O arquivo `.env` é ignorado pelo Git. Confirme que o **Working directory** da
configuração da aplicação aponta para a raiz do projeto; o IntelliJ não herda
automaticamente variáveis exportadas em outro terminal.

### 3. Gerar as chaves JWT locais

O login usa chaves RSA. Gere o par em `src/main/resources/keys`; essa pasta é
ignorada pelo Git:

```bash
mkdir -p src/main/resources/keys
openssl genpkey -algorithm RSA -out src/main/resources/keys/private_key.pem -pkeyopt rsa_keygen_bits:2048
openssl rsa -pubout -in src/main/resources/keys/private_key.pem -out src/main/resources/keys/public_key.pem
```

As chaves são carregadas do sistema de arquivos, sem caminho absoluto de máquina. Se já
houver um par válido nesse diretório, não o substitua: trocar as chaves invalida
os tokens anteriormente emitidos. As chaves privadas ficam fora do JAR. Se a
aplicação for iniciada de outro diretório, configure `JWT_PRIVATE_KEY` e
`JWT_PUBLIC_KEY` com os caminhos dos dois arquivos.

### 4. Iniciar

Na raiz do repositório, para informar a senha interativamente sem exibi-la:

```bash
./scripts/run-local.sh
```

O script usa `hackathon_setic_local` por padrão. Para selecionar outro banco ou
usuário, defina `DB_URL` e `DB_USERNAME` antes de iniciar. Também é possível
configurar `DB_PASSWORD` previamente, sem que o script solicite a senha.

Também é possível configurar `DB_URL`, `DB_USERNAME` e `DB_PASSWORD` em
**Run > Edit Configurations > Environment variables**.

Para criar e executar o JAR:

```bash
./mvnw clean package
java -jar target/hackathon-setic-2026-0.0.1-SNAPSHOT.jar
```

Abra `http://localhost:8080/login.html` e autentique-se com uma conta cadastrada
no banco configurado. Não há usuário ou senha de demonstração criado pela aplicação.
O cargo cadastrado determina o painel para o qual o login redireciona.

<<<<<<< Updated upstream
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
=======
## Arquitetura

```text
src/main/java/br/com/hackathon/setic/
├── config/       # Segurança, JWT e configurações
├── controller/   # Endpoints REST
├── dto/          # Contratos de requisição e resposta
├── model/        # Entidades JPA
├── repository/   # Acesso a dados
└── service/      # Regras de negócio
```

Os HTML em `src/main/resources/static/` usam os endpoints em `/api/v1` para
consultar e atualizar riscos, contextos, PRRs e pareceres, conforme o cargo
autenticado.
>>>>>>> Stashed changes

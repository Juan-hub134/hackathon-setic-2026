#!/usr/bin/env bash
set -euo pipefail

if [[ $# -lt 1 || $# -gt 2 ]]; then
    echo "Uso: $0 <backup.sql> [nome_do_banco]" >&2
    exit 2
fi

dump_file=$1
database=${2:-${DB_NAME:-hackathon_setic_local}}

if [[ ! -f "$dump_file" ]]; then
    echo "Arquivo SQL não encontrado: $dump_file" >&2
    exit 2
fi

if [[ ! "$database" =~ ^[A-Za-z_][A-Za-z0-9_]*$ ]]; then
    echo "Nome de banco inválido: use apenas letras, números e underscore." >&2
    exit 2
fi

if ! command -v createdb >/dev/null || ! command -v psql >/dev/null; then
    echo "PostgreSQL client tools (createdb e psql) precisam estar instalados." >&2
    exit 2
fi

if ! pg_isready -h "${PGHOST:-localhost}" -p "${PGPORT:-5432}" >/dev/null; then
    echo "PostgreSQL não está aceitando conexões em ${PGHOST:-localhost}:${PGPORT:-5432}." >&2
    exit 1
fi

createdb --maintenance-db=postgres --template=template0 "$database"

# Remove diretivas do pg_dump 18 que clientes/servidores PostgreSQL 16 não reconhecem.
sed -e '/^\\restrict[[:space:]]/d' \
    -e '/^\\unrestrict[[:space:]]/d' \
    -e '/^SET transaction_timeout = /d' \
    "$dump_file" |
    psql --set=ON_ERROR_STOP=1 --single-transaction --dbname="$database"

echo "Banco $database restaurado. O dump original não foi alterado."

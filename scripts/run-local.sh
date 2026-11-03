#!/usr/bin/env bash
set -euo pipefail

if [[ -z "${DB_PASSWORD:-}" ]]; then
    if ! read -r -s -p "Senha do PostgreSQL (${DB_USERNAME:-postgres}): " DB_PASSWORD; then
        printf '\n' >&2
        echo "Não foi possível ler a senha do PostgreSQL." >&2
        exit 2
    fi
    printf '\n'
fi

if [[ -z "${DB_PASSWORD:-}" ]]; then
    echo "DB_PASSWORD não pode ficar vazio. Informe a senha do PostgreSQL." >&2
    exit 2
fi

export DB_PASSWORD
export DB_USERNAME="${DB_USERNAME:-postgres}"
export DB_URL="${DB_URL:-jdbc:postgresql://localhost:5432/hackathon_setic_local}"

exec ./mvnw spring-boot:run

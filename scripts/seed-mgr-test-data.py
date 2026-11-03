#!/usr/bin/env python3
import os
import subprocess
import sys
from pathlib import Path
from urllib.parse import parse_qs, unquote, urlsplit


ROOT = Path(__file__).resolve().parent.parent
SEED_FILE = ROOT / "scripts" / "seed-mgr-test-data.sql"


def read_dotenv(path: Path) -> dict[str, str]:
    values = {}
    if not path.is_file():
        return values
    for raw_line in path.read_text(encoding="utf-8").splitlines():
        line = raw_line.strip()
        if not line or line.startswith("#") or "=" not in line:
            continue
        key, value = line.split("=", 1)
        value = value.strip()
        if len(value) >= 2 and value[0] == value[-1] and value[0] in {"'", '"'}:
            value = value[1:-1]
        values[key.strip()] = value
    return values


def main() -> int:
    dry_run = sys.argv[1:] == ["--dry-run"]
    if sys.argv[1:] not in ([], ["--dry-run"]):
        print(f"Uso: {Path(sys.argv[0]).name} [--dry-run]", file=sys.stderr)
        return 2

    dotenv = read_dotenv(ROOT / ".env")
    db_url = os.environ.get("DB_URL", dotenv.get("DB_URL", ""))
    db_username = os.environ.get("DB_USERNAME", dotenv.get("DB_USERNAME", ""))
    db_password = os.environ.get("DB_PASSWORD", dotenv.get("DB_PASSWORD", ""))
    if not db_url.startswith("jdbc:postgresql://"):
        print("DB_URL precisa apontar para uma conexão PostgreSQL.", file=sys.stderr)
        return 2
    if not db_username or not db_password:
        print("Defina DB_USERNAME e DB_PASSWORD no ambiente ou no .env da raiz.", file=sys.stderr)
        return 2

    parsed = urlsplit(db_url.removeprefix("jdbc:"))
    database = unquote(parsed.path.lstrip("/"))
    if not parsed.hostname or not database or parsed.username or parsed.password:
        print("DB_URL deve ter host e banco, sem credenciais embutidas na URL.", file=sys.stderr)
        return 2

    child_env = os.environ.copy()
    child_env.update({
        "PGHOST": parsed.hostname,
        "PGPORT": str(parsed.port or 5432),
        "PGDATABASE": database,
        "PGUSER": db_username,
        "PGPASSWORD": db_password,
    })
    sslmode = parse_qs(parsed.query).get("sslmode")
    if sslmode:
        child_env["PGSSLMODE"] = sslmode[0]

    psql = [
        "psql",
        "--no-psqlrc",
        "--set=ON_ERROR_STOP=1",
        "--dbname",
        database,
    ]
    summary = """
SELECT 'riscos=' || count(*) FROM public.risco WHERE causa LIKE 'TESTE_MGR_R%';
SELECT 'contextos=' || count(*) FROM public.contexto WHERE escopo LIKE 'TESTE MGR %';
SELECT 'etapas=' || count(*) FROM public.contexto_etapa WHERE etapa LIKE 'TESTE MGR %';
SELECT 'prrs=' || count(*) FROM public.plano_resposta WHERE nome_responsaveis LIKE 'TESTE MGR %';
SELECT 'pareceres=' || count(*) FROM public.parecer_prr WHERE parecer_texto LIKE 'TESTE_MGR_PARECER_%';
"""
    try:
        if dry_run:
            result = subprocess.run(
                psql + ["--tuples-only", "--no-align", "-c", "BEGIN",
                        "--file", str(SEED_FILE), "-c", summary, "-c", "ROLLBACK"],
                cwd=ROOT, env=child_env, check=True, text=True, capture_output=True,
            )
            print("Simulação concluída; nenhuma alteração foi gravada.")
            if result.stdout.strip():
                print(result.stdout.strip())
        else:
            subprocess.run(
                psql + ["--single-transaction", "--file", str(SEED_FILE)],
                cwd=ROOT, env=child_env, check=True,
            )
            result = subprocess.run(
                psql + ["--tuples-only", "--no-align", "-c", summary],
                cwd=ROOT, env=child_env, check=True, text=True, capture_output=True,
            )
            print(f"Dados fictícios MGR carregados em {database}:")
            print(result.stdout.strip())
    except FileNotFoundError:
        print("psql não encontrado. Instale as ferramentas cliente do PostgreSQL.", file=sys.stderr)
        return 2
    except subprocess.CalledProcessError as error:
        if error.stderr:
            print(error.stderr.strip(), file=sys.stderr)
        print("Carga não concluída; a transação foi revertida.", file=sys.stderr)
        return error.returncode or 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())

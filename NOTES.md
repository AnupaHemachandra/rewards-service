# Notes

## Plan and key decisions

- Add database-backed idempotency records and associate successful redemption ledger entries with their request key.
- Lock the account row in the database before balance validation and deduction; do not use in-memory locks, since requests may reach different service instances.
- Use the globally unique `idempotency_record.idempotency_key` primary key to arbitrate duplicate requests. Persist the balance change, successful response, and `REDEEM` transaction in one database transaction.
- Keep the transaction key nullable for existing `EARN` entries, and uniquely index non-null keys so one key cannot produce multiple transaction entries.
- Store ledger `points` as signed balance deltas: positive for `EARN`, negative for `REDEEM`. The request still supplies a positive redemption amount.
- Use `VARCHAR(255)` for idempotency keys and store the SHA-256 request hash as 64 hexadecimal characters; store the original response as PostgreSQL `JSONB`.

## How I used AI

| Step | Tool | What I asked | Was it right? What did I change? |
|---|---|---|---|
| Phase 1 schema design | GitHub Copilot | Review the assignment, repository rules, and current schema; draft idempotency and redemption transaction storage. | Identified that `CREATE TABLE IF NOT EXISTS` does not upgrade an existing `points_transaction` table, so the schema uses an additive `ALTER TABLE ... ADD COLUMN IF NOT EXISTS`. |

## How I know it works

- `mvn test`: 3 existing PostgreSQL/Testcontainers earn integration tests passed; schema initialization succeeded on a fresh PostgreSQL 16 database.
- The existing-table upgrade path uses `ADD COLUMN IF NOT EXISTS` but is not directly covered by the current tests.

## What I'd do with more time

-

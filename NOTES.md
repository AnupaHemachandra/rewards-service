# Notes

## Plan and key decisions

- Add database-backed idempotency records and associate successful redemption ledger entries with their request key.
- Lock the account row in the database before balance validation and deduction; do not use in-memory locks, since requests may reach different service instances.
- Use the globally unique `idempotency_record.idempotency_key` primary key to arbitrate duplicate requests. Persist the balance change, successful response, and `REDEEM` transaction in one database transaction.
- Keep the transaction key nullable for existing `EARN` entries, and uniquely index non-null keys so one key cannot produce multiple transaction entries.
- Store ledger `points` as signed balance deltas: positive for `EARN`, negative for `REDEEM`. The request still supplies a positive redemption amount.
- Use `VARCHAR(255)` for idempotency keys and store the SHA-256 request hash as 64 hexadecimal characters; store the original response as PostgreSQL `JSONB`.
- Use R2DBC `SELECT ... FOR UPDATE` for account row locking. The requested JPA `@Lock` annotation is not applicable because this service uses `DatabaseClient` and Spring Data R2DBC, not JPA.
- Map redemption domain exceptions to RFC 7807 statuses through the existing `ApiExceptionHandler`.

## How I used AI

| Step | Tool | What I asked | Was it right? What did I change? |
|---|---|---|---|
| Phase 1 schema design | GitHub Copilot | Review the assignment, repository rules, and current schema; draft idempotency and redemption transaction storage. | Identified that `CREATE TABLE IF NOT EXISTS` does not upgrade an existing `points_transaction` table, so the schema uses an additive `ALTER TABLE ... ADD COLUMN IF NOT EXISTS`. |
| Phase 2 persistence and exceptions | GitHub Copilot | Add persistence repositories, the account row-lock query, and domain exceptions/status mappings. | Replaced the requested JPA `@Lock` approach with equivalent `SELECT ... FOR UPDATE` SQL to match the app's existing R2DBC architecture. An initial JSON codec reference failed because the driver is runtime-scoped; switched to SQL `CAST` for JSONB binding. |
| Phase 4 concurrency tests | GitHub Copilot | Add simultaneous redemption and same-key retry tests. | The database FK failure exposed that the idempotency row must be saved before its ledger row. The retry test also exposed a missing idempotency recheck after waiting for the account lock; both were corrected in the service. |

## How I know it works

- `mvn test`: 3 existing PostgreSQL/Testcontainers earn integration tests passed; schema initialization succeeded on a fresh PostgreSQL 16 database.
- The existing-table upgrade path uses `ADD COLUMN IF NOT EXISTS` but is not directly covered by the current tests.
- Phase 2 `mvn test`: the same 3 tests passed, confirming Java compilation and Spring context startup. New repository SQL methods are not yet directly exercised; add focused repository/redemption tests in later phases.
- Phase 4 focused concurrency tests: 2 passed against PostgreSQL Testcontainers, proving 3/10 unique redemptions succeed from a 300-point balance and all 5 same-key retries replay one redemption from a 100-point balance.
- Full `mvn test` after Phase 4: all 5 tests passed (3 earn integration tests and 2 redemption concurrency tests).

## What I'd do with more time

-

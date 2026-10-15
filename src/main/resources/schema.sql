-- Applied on startup (spring.sql.init.mode=always). Safe to re-run.

CREATE TABLE IF NOT EXISTS points_account (
    id          VARCHAR(64)  PRIMARY KEY,
    balance     BIGINT       NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- Append-only history of every balance change.
CREATE TABLE IF NOT EXISTS points_transaction (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    account_id  VARCHAR(64)  NOT NULL REFERENCES points_account (id),
    type        VARCHAR(16)  NOT NULL,          -- EARN, REDEEM
    points      BIGINT       NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_points_transaction_account ON points_transaction (account_id);

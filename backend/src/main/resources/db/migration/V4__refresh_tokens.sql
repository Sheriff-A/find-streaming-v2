CREATE TABLE refresh_tokens
(
    id         uuid PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
    user_id    uuid             NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    token_hash TEXT             NOT NULL,
    expires_at TIMESTAMPTZ      NOT NULL,
    revoked_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ               DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX idx_refresh_tokens_token_hash ON refresh_tokens (token_hash);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);
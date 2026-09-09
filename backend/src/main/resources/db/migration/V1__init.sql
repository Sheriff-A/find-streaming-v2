CREATE TABLE users
(
    id             uuid PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
    email          VARCHAR(255)     NOT NULL,
    password_hash  TEXT NULL,
    email_verified BOOLEAN                   DEFAULT FALSE,
    created_at     TIMESTAMPTZ               DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMPTZ               DEFAULT CURRENT_TIMESTAMP,
    status         TEXT                      DEFAULT 'ACTIVE' CHECK ( status IN ('ACTIVE', 'INACTIVE') )
);

CREATE UNIQUE INDEX idx_users_email ON users (LOWER(email));

CREATE TABLE user_roles
(
    user_id uuid         NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role    VARCHAR(255) NOT NULL DEFAULT 'USER' check (role in ('USER', 'ADMIN')),
    PRIMARY KEY (user_id, role)
);

CREATE TABLE profiles
(
    id           uuid NOT NULL PRIMARY KEY REFERENCES users (id) ON DELETE CASCADE,
    display_name VARCHAR(255),
    avatar_url   VARCHAR(255),
    bio          VARCHAR(255),
    preferences  JSONB       DEFAULT '{}',
    created_at   TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE watchlist
(
    id          uuid PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
    owner_id    uuid             NOT NULL REFERENCES profiles (id) ON DELETE CASCADE,
    name        VARCHAR(255)     NOT NULL,
    description VARCHAR(255),
    visibility  VARCHAR(255)              DEFAULT 'PRIVATE' CHECK (visibility IN ('PUBLIC', 'PRIVATE', 'UNLISTED')),
    created_at  TIMESTAMPTZ               DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ               DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_watchlist_name UNIQUE (owner_id, name)
);

CREATE INDEX idx_watchlist_owner_id ON watchlist (owner_id);
CREATE INDEX idx_watchlist_name ON watchlist (name);

CREATE TABLE watchlist_items
(
    id           uuid PRIMARY KEY NOT NULL DEFAULT gen_random_uuid(),
    watchlist_id uuid             NOT NULL REFERENCES watchlist (id) ON DELETE CASCADE,
    media_id     VARCHAR(255)     NOT NULL,
    media_type   VARCHAR(255)     NOT NULL CHECK (media_type IN ('MOVIE', 'TV_SHOW')),
    position     INTEGER          NOT NULL DEFAULT 0,
    metadata     JSONB                     DEFAULT '{}',
    created_at   TIMESTAMPTZ               DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMPTZ               DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_watchlist_item UNIQUE (watchlist_id, media_id, media_type)
);

CREATE INDEX idx_watchlist_items_watchlist_id ON watchlist_items (watchlist_id);
CREATE INDEX idx_watchlist_items_media_id ON watchlist_items (media_id);
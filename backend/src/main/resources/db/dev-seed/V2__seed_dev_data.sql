-- Local-dev-only seed data. Lives in a separate Flyway location (db/dev-seed)
-- from the real schema (db/migration), wired in only for the `local` and
-- `docker` Spring profiles - never applies to an environment that only points
-- Flyway at db/migration.
--
-- All seeded users share the password "password123" (the bcrypt hash below), so
-- there's something to log in with once auth is built.
-- This is fine for throwaway local data; NEVER for real accounts.

INSERT INTO users (id, email, password_hash, email_verified, status)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'admin@findstreaming.local', '$2b$10$USZGppQnsnJESOH/eMM8h.Tyz6qX8FzMnhtnALj892gkacWnpYaiG', true, 'ACTIVE'),
    ('00000000-0000-0000-0000-000000000002', 'alice@findstreaming.local', '$2b$10$USZGppQnsnJESOH/eMM8h.Tyz6qX8FzMnhtnALj892gkacWnpYaiG', true, 'ACTIVE'),
    ('00000000-0000-0000-0000-000000000003', 'bob@findstreaming.local', '$2b$10$USZGppQnsnJESOH/eMM8h.Tyz6qX8FzMnhtnALj892gkacWnpYaiG', true, 'ACTIVE');

INSERT INTO user_roles (user_id, role)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'ADMIN'),
    ('00000000-0000-0000-0000-000000000001', 'USER'),
    ('00000000-0000-0000-0000-000000000002', 'USER'),
    ('00000000-0000-0000-0000-000000000003', 'USER');

INSERT INTO profiles (id, display_name, bio)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'Admin', 'Seed admin account'),
    ('00000000-0000-0000-0000-000000000002', 'Alice', 'Seed test account'),
    ('00000000-0000-0000-0000-000000000003', 'Bob', 'Seed test account');

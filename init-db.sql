
CREATE TABLE IF NOT EXISTS "user" (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(500) NOT NULL,
    date_of_birth DATE         NOT NULL,
    password      VARCHAR(500) NOT NULL
);

CREATE TABLE IF NOT EXISTS account (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT         NOT NULL UNIQUE REFERENCES "user" (id) ON DELETE CASCADE,
    balance         DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    initial_balance DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    CONSTRAINT chk_balance_non_negative CHECK (balance >= 0)
);

CREATE TABLE IF NOT EXISTS email_data (
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT       NOT NULL REFERENCES "user" (id) ON DELETE CASCADE,
    email   VARCHAR(200) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS phone_data (
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT      NOT NULL REFERENCES "user" (id) ON DELETE CASCADE,
    phone   VARCHAR(13) NOT NULL UNIQUE
);

CREATE INDEX IF NOT EXISTS idx_email_data_user_id ON email_data (user_id);
CREATE INDEX IF NOT EXISTS idx_phone_data_user_id ON phone_data (user_id);
CREATE INDEX IF NOT EXISTS idx_user_name            ON "user" (name);
CREATE INDEX IF NOT EXISTS idx_user_dob             ON "user" (date_of_birth);

INSERT INTO "user" (name, date_of_birth, password) VALUES
    ('Иван Иванов',     '1990-05-01', '$2y$10$lnEccNo7THRv8eQefrpxl.p.4STGSN8n2M9h1ClBJpyHwuQeQXOTK'),
    ('Мария Петрова',   '1985-03-15', '$2y$10$lnEccNo7THRv8eQefrpxl.p.4STGSN8n2M9h1ClBJpyHwuQeQXOTK'),
    ('Алексей Сидоров', '1995-11-22', '$2y$10$lnEccNo7THRv8eQefrpxl.p.4STGSN8n2M9h1ClBJpyHwuQeQXOTK')
ON CONFLICT DO NOTHING;

INSERT INTO account (user_id, balance, initial_balance) VALUES
    (1, 1000.00, 1000.00),
    (2, 5000.00, 5000.00),
    (3,  250.00,  250.00)
ON CONFLICT DO NOTHING;

INSERT INTO email_data (user_id, email) VALUES
    (1, 'ivan@example.com'),
    (1, 'ivan.work@example.com'),
    (2, 'maria@example.com'),
    (3, 'alexey@example.com')
ON CONFLICT DO NOTHING;

INSERT INTO phone_data (user_id, phone) VALUES
    (1, '79207865432'),
    (1, '79101234567'),
    (2, '79309876543'),
    (3, '79501112233')
ON CONFLICT DO NOTHING;

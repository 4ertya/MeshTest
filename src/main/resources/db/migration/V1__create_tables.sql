
CREATE TABLE "user" (
    id            BIGSERIAL PRIMARY KEY,
    name          VARCHAR(500) NOT NULL,
    date_of_birth DATE         NOT NULL,
    password      VARCHAR(500) NOT NULL
);


CREATE TABLE account (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT         NOT NULL UNIQUE REFERENCES "user" (id) ON DELETE CASCADE,
    balance         DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    initial_balance DECIMAL(19, 2) NOT NULL DEFAULT 0.00,
    CONSTRAINT chk_balance_non_negative CHECK (balance >= 0)
);


CREATE TABLE email_data (
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT       NOT NULL REFERENCES "user" (id) ON DELETE CASCADE,
    email   VARCHAR(200) NOT NULL UNIQUE
);


CREATE TABLE phone_data (
    id      BIGSERIAL PRIMARY KEY,
    user_id BIGINT      NOT NULL REFERENCES "user" (id) ON DELETE CASCADE,
    phone   VARCHAR(13) NOT NULL UNIQUE
);


CREATE INDEX idx_email_data_user_id ON email_data (user_id);
CREATE INDEX idx_phone_data_user_id ON phone_data (user_id);
CREATE INDEX idx_user_name ON "user" (name);
CREATE INDEX idx_user_dob ON "user" (date_of_birth);

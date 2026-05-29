-- password123
INSERT INTO "user" (id, name, date_of_birth, password) VALUES
    (1, 'Иван Иванов',    '1990-05-01', '$2y$10$lnEccNo7THRv8eQefrpxl.p.4STGSN8n2M9h1ClBJpyHwuQeQXOTK'),
    (2, 'Мария Петрова',  '1985-03-15', '$2y$10$lnEccNo7THRv8eQefrpxl.p.4STGSN8n2M9h1ClBJpyHwuQeQXOTK'),
    (3, 'Алексей Сидоров','1995-11-22', '$2y$10$lnEccNo7THRv8eQefrpxl.p.4STGSN8n2M9h1ClBJpyHwuQeQXOTK');

INSERT INTO account (user_id, balance, initial_balance) VALUES
    (1, 1000.00, 1000.00),
    (2, 5000.00, 5000.00),
    (3, 250.00,  250.00);

INSERT INTO email_data (user_id, email) VALUES
    (1, 'ivan@example.com'),
    (1, 'ivan.work@example.com'),
    (2, 'maria@example.com'),
    (3, 'alexey@example.com');

INSERT INTO phone_data (user_id, phone) VALUES
    (1, '79207865432'),
    (1, '79101234567'),
    (2, '79309876543'),
    (3, '79501112233');

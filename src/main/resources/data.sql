-- Користувачі
MERGE INTO users (username, email, password, role)
KEY (username) VALUES ('john_doe', 'john@example.com', 'user123', 'USER'),
                      ('admin', 'admin@example.com', 'admin123', 'ADMIN');

-- Завдання
MERGE INTO tasks (title, description, deadline, status, user_id)
KEY (title) VALUES ('Complete homework', 'Finish math and science homework', '2023-12-01 18:00:00', 'PENDING', 1),
                   ('Fix server', 'Resolve critical issue on production server', '2023-11-25 18:00:00', 'IN_PROGRESS', 2);
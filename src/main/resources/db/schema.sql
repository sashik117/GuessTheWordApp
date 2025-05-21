-- Очистка існуючих таблиць
DROP TABLE IF EXISTS WordStats;
DROP TABLE IF EXISTS Guess;
DROP TABLE IF EXISTS GameSession;
DROP TABLE IF EXISTS Word;
DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS Hint;

-- Повторне створення всіх таблиць
CREATE TABLE User (
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      username TEXT NOT NULL,
                      email TEXT NOT NULL,
                      password_hash TEXT NOT NULL,
                      role TEXT DEFAULT 'PLAYER'
);

INSERT INTO User (username, email, password_hash, role) VALUES
                         ('admin', 'admin@example.com', 'hashed_password_123', 'ADMIN'),
                         ('user1', 'user1@example.com', 'hashed_pass_456', 'PLAYER');

CREATE TABLE Word (
                      word_id INTEGER PRIMARY KEY AUTOINCREMENT,
                      text TEXT NOT NULL,
                      difficulty INTEGER,
                      language TEXT
);

INSERT INTO Word (text, difficulty, language) VALUES
                         ('яблуко', 1, 'uk'),
                         ('сонце', 1, 'uk'),
                          ('гравітація', 3, 'uk');

CREATE TABLE Hint (
                      hint_id INTEGER PRIMARY KEY AUTOINCREMENT,
                      word_id INTEGER NOT NULL,
                      hint_text TEXT NOT NULL,
                      FOREIGN KEY (word_id) REFERENCES Word(word_id)
);

INSERT INTO Hint (word_id, hint_text) VALUES
                         (1, 'Фрукт, що росте на дереві'),
                         (2, 'Світить вдень'),
                         (3, 'Сила, що притягує');

CREATE TABLE GameSession (
                             session_id INTEGER PRIMARY KEY AUTOINCREMENT,
                             user_id INTEGER NOT NULL,
                             start_time TEXT NOT NULL,
                             end_time TEXT DEFAULT NULL, -- end_time може бути NULL
                             FOREIGN KEY (user_id) REFERENCES User(id)
);

INSERT INTO GameSession (user_id, start_time, end_time) VALUES
    (2, '2025-05-06T14:00:00', NULL); -- end_time можна залишити NULL

CREATE TABLE Guess (
                       guess_id INTEGER PRIMARY KEY AUTOINCREMENT,
                       session_id INTEGER NOT NULL,
                       word_id INTEGER NOT NULL,
                       guess_text TEXT NOT NULL,
                       is_correct INTEGER NOT NULL,
                       guess_time TEXT NOT NULL,
                       FOREIGN KEY (session_id) REFERENCES GameSession(session_id),
                       FOREIGN KEY (word_id) REFERENCES Word(word_id)
);

INSERT INTO Guess (session_id, word_id, guess_text, is_correct, guess_time) VALUES
                     (1, 1, 'груша', 0, '2025-05-06T14:02:00'),
                     (1, 1, 'яблуко', 1, '2025-05-06T14:03:00');

CREATE TABLE WordStats (
                           stat_id INTEGER PRIMARY KEY AUTOINCREMENT,
                           word_id INTEGER NOT NULL,
                           correct_count INTEGER,
                           total_count INTEGER,
                           FOREIGN KEY (word_id) REFERENCES Word(word_id)
);

-- Заповнення таблиці WordStats на основі існуючих даних
INSERT INTO WordStats (word_id, correct_count, total_count) VALUES
                      (1, 1, 2), -- "яблуко": 1 правильна спроба, 2 загальні спроби ("груша" і "яблуко")
                     (2, 0, 0), -- "сонце": поки що не використовувалось
                      (3, 0, 0); -- "гравітація": поки що не використовувалось



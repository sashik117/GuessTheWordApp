INSERT INTO User (username, email, password_hash, role) VALUES
                                                            ('admin', 'admin@example.com', 'hashed_password_123', 'ADMIN'),
                                                            ('user1', 'user1@example.com', 'hashed_pass_456', 'PLAYER');

INSERT INTO Word (text, difficulty, language) VALUES
                                                  ('яблуко', 1, 'uk'),
                                                  ('сонце', 1, 'uk'),
                                                  ('гравітація', 3, 'uk');

INSERT INTO Hint (word_id, hint_text) VALUES
                                          (1, 'Фрукт, що росте на дереві'),
                                          (2, 'Світить вдень'),
                                          (3, 'Сила, що притягує');

INSERT INTO GameSession (user_id, start_time, end_time) VALUES
    (2, '2025-05-06T14:00:00', NULL);

INSERT INTO Guess (session_id, word_id, guess_text, is_correct, guess_time) VALUES
                                                                                (1, 1, 'груша', 0, '2025-05-06T14:02:00'),
                                                                                (1, 1, 'яблуко', 1, '2025-05-06T14:03:00');

INSERT INTO WordStats (word_id, correct_count, total_count) VALUES
                                                                (1, 1, 2),
                                                                (2, 0, 0),
                                                                (3, 0, 0);

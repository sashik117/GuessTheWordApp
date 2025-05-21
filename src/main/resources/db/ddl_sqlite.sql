DROP TABLE IF EXISTS WordStats;
DROP TABLE IF EXISTS Guess;
DROP TABLE IF EXISTS GameSession;
DROP TABLE IF EXISTS Word;
DROP TABLE IF EXISTS User;
DROP TABLE IF EXISTS Hint;

CREATE TABLE User (
                      id INTEGER PRIMARY KEY AUTOINCREMENT,
                      username TEXT NOT NULL,
                      email TEXT NOT NULL,
                      password_hash TEXT NOT NULL,
                      role TEXT DEFAULT 'PLAYER'
);

CREATE TABLE Word (
                      word_id INTEGER PRIMARY KEY AUTOINCREMENT,
                      text TEXT NOT NULL,
                      difficulty INTEGER NOT NULL,
                      language TEXT NOT NULL,
                      description TEXT
);

CREATE TABLE Hint (
                      hint_id INTEGER PRIMARY KEY AUTOINCREMENT,
                      word_id INTEGER NOT NULL,
                      hint_text TEXT NOT NULL,
                      FOREIGN KEY (word_id) REFERENCES Word(word_id)
);

CREATE TABLE GameSession (
                             session_id INTEGER PRIMARY KEY AUTOINCREMENT,
                             user_id INTEGER NOT NULL,
                             start_time TEXT NOT NULL,
                             end_time TEXT DEFAULT NULL,
                             FOREIGN KEY (user_id) REFERENCES User(id)
);

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

CREATE TABLE WordStats (
                           stat_id INTEGER PRIMARY KEY AUTOINCREMENT,
                           word_id INTEGER NOT NULL,
                           correct_count INTEGER,
                           total_count INTEGER,
                           FOREIGN KEY (word_id) REFERENCES Word(word_id)
);

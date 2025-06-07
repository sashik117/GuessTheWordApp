// GameSessionStats.java
package com.guessthewordapp.presentation.gameservice;

import java.time.LocalDateTime;

public class GameSessionStats {
    private final boolean win;
    private final int wordsGuessed;
    private final int totalWords;
    private final LocalDateTime timestamp;

    public GameSessionStats(boolean win, int wordsGuessed, int totalWords, LocalDateTime timestamp) {
        this.win = win;
        this.wordsGuessed = wordsGuessed;
        this.totalWords = totalWords;
        this.timestamp = timestamp;
    }

    // Геттери
    public boolean isWin() { return win; }
    public int getWordsGuessed() { return wordsGuessed; }
    public int getTotalWords() { return totalWords; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
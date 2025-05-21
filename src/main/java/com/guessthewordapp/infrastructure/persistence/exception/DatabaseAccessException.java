package com.guessthewordapp.infrastructure.persistence.exception;

/**
 * Сигналізує про помилки під час ініціалізації бази даних.
 */
public class DatabaseAccessException extends RuntimeException {

    public DatabaseAccessException(String message) {
        super(message);
    }

    public DatabaseAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
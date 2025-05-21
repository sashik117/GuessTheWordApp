package com.guessthewordapp.infrastructure.persistence.exception;

/**
 * Виникає, коли трапляється помилка під час перетворення між об'єктами програми та записами в сховищі даних.
 */
public class DataMappingException extends RuntimeException {

    public DataMappingException(String message) {
        super(message);
    }

    public DataMappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
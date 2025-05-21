package com.guessthewordapp.infrastructure.persistence.exception;

/**
 * Сигналізує про помилки, пов'язані з доступом до сховища даних.
 */
public class DataStorageException extends RuntimeException {

  public DataStorageException(String message) {
    super(message);
  }

  public DataStorageException(String message, Throwable cause) {
    super(message, cause);
  }
}
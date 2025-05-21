package com.guessthewordapp.application.contract.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.sql.Timestamp;

/**
 * DTO для сутності GameSession.
 */
public record GameSessionDTO(

    Long id, // Може бути nullable, якщо це нова сесія

    @NotNull(message = "Ідентифікатор користувача не може бути порожнім")
    Long userId,

    @NotNull(message = "Час початку сесії не може бути порожнім")
    @PastOrPresent(message = "Час початку сесії має бути в минулому або теперішньому")
    Timestamp startedAt,

    @PastOrPresent(message = "Час закінчення сесії має бути в минулому або теперішньому")
    Timestamp endedAt
) {}

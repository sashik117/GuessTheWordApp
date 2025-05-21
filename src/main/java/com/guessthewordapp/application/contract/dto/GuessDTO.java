package com.guessthewordapp.application.contract.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record GuessDTO(

    Long id,  // можна залишити без валідації, це ідентифікатор

    @NotNull(message = "Session ID не може бути null")
    Long sessionId,

    @NotNull(message = "Word ID не може бути null")
    Long wordId,

    @NotNull(message = "Guessed text не може бути null")
    @Size(min = 1, max = 100, message = "Guessed text має бути від 1 до 100 символів")
    String guessedText,

    boolean correct

) {}

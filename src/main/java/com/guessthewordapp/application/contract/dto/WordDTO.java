package com.guessthewordapp.application.contract.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record WordDTO(
    Integer id,

    @NotBlank(message = "Текст слова не може бути порожнім")
    @Size(min = 2, max = 50, message = "Слово має містити від 2 до 50 символів")
    String text,

    @PositiveOrZero(message = "Складність не може бути від'ємною")
    int difficulty,

    @NotBlank(message = "Мова не може бути порожньою")
    String language,

    @Size(max = 255, message = "Опис не може перевищувати 255 символів")
    String description
) {}
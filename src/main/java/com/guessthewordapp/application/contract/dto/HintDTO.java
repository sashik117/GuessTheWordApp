package com.guessthewordapp.application.contract.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO для підказки до слова
 */
public record HintDTO(
    Long id,
    @NotNull(message = "Ідентифікатор слова обов'язковий")
    Long wordId,
    @NotBlank(message = "Текст підказки не може бути порожнім")
    String text
) {}
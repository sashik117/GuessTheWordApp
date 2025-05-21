package com.guessthewordapp.application.contract.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record WordStatsDTO(
    Long id,

    @NotNull(message = "ID слова обов'язковий")
    Long wordId,

    @Min(value = 0, message = "Кількість вгадувань не може бути від'ємною")
    int correctCount,

    @Min(value = 0, message = "Загальна кількість не може бути від'ємною")
    int totalCount
) {
    public double getSuccessRate() {
        return totalCount > 0 ? (correctCount * 100.0) / totalCount : 0;
    }
}
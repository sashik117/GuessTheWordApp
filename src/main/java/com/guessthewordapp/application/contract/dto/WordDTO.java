package com.guessthewordapp.application.contract.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public class WordDTO { // Змінили на class

    private Integer id;

    @NotBlank(message = "Текст слова не може бути порожнім")
    @Size(min = 2, max = 50, message = "Слово має містити від 2 до 50 символів")
    private String text;

    @PositiveOrZero(message = "Складність не може бути від'ємною")
    private int difficulty;

    @NotBlank(message = "Мова не може бути порожньою")
    private String language;

    @Size(max = 255, message = "Опис не може перевищувати 255 символів")
    private String description;

    // Конструктор
    public WordDTO(Integer id, String text, int difficulty, String language, String description) {
        this.id = id;
        this.text = text;
        this.difficulty = difficulty;
        this.language = language;
        this.description = description;
    }

    // --- ГЕТТЕРИ (ДУЖЕ ВАЖЛИВО ДЛЯ PropertyValueFactory) ---
    public Integer getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getLanguage() {
        return language;
    }

    public String getDescription() {
        return description;
    }

    // --- СЕТТЕРИ (можливо, знадобляться для форм або інших операцій) ---
    // Якщо WordDTO використовується як незмінний DTO, сеттери можуть бути не потрібні,
    // але якщо ти його змінюєш через binding до форми, вони будуть необхідні.
    // Якщо ти використовуєш Property's у ViewModel, то сеттери не потрібні для UI,
    // але можуть бути потрібні для JPA Entity або для створення DTO.
    public void setId(Integer id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    // ОПЦІЙНО: перевизначити toString() для кращого логування
    @Override
    public String toString() {
        return "WordDTO{" +
            "id=" + id +
            ", text='" + text + '\'' +
            ", difficulty=" + difficulty +
            ", language='" + language + '\'' +
            ", description='" + description + '\'' +
            '}';
    }
}
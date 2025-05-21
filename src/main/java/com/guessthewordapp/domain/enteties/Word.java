package com.guessthewordapp.domain.enteties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Word {
    private Integer id;
    private String text;
    private int difficulty;
    private String language;
    private String description;

    // Додайте цей конструктор
    public Word(String text, int difficulty, String language) {
        this.text = text;
        this.difficulty = difficulty;
        this.language = language;
    }
}
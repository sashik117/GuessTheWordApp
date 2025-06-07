package com.guessthewordapp.domain.enteties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Word")
@Getter
@Setter
@NoArgsConstructor // Конструктор без аргументів
@ToString
@EqualsAndHashCode
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "word_id")
    private Integer id; // <--- ID ТИП - Integer, відповідно до ваших вимог!

    @Column(name = "text")
    private String text;

    @Column(name = "difficulty")
    private int difficulty;

    @Column(name = "language")
    private String language;

    @Column(name = "description")
    private String description;

    // Ваш конструктор з 3 аргументами
    public Word(String text, int difficulty, String language) {
        this.text = text;
        this.difficulty = difficulty;
        this.language = language;
        // id та description за замовчуванням null
    }

    // Ручний AllArgsConstructor, щоб Lombok не створював свій і не конфліктував
    public Word(Integer id, String text, int difficulty, String language, String description) {
        this.id = id;
        this.text = text;
        this.difficulty = difficulty;
        this.language = language;
        this.description = description;
    }
}
package com.guessthewordapp.domain.enteties;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor; // <--- ЦЯ АНОТАЦІЯ ВИРІШУЄ ПРОБЛЕМУ З КОНСТРУКТОРОМ БЕЗ АРГУМЕНТІВ

@Entity
@Table(name = "WordStats")
@Data // Генерує геттери, сетери, toString, equals, hashCode
@NoArgsConstructor // Явно вказуємо Lombok створити конструктор без аргументів
@AllArgsConstructor // Явно вказуємо Lombok створити конструктор з усіма полями
public class WordStats {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stat_id")
    private Long id;

    @Column(name = "word_id")
    private Long wordId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "correct_count")
    private Integer correctCount;

    @Column(name = "total_count")
    private Integer totalCount;

    // ВИДАЛІТЬ УСІ ВРУЧНУ ПРОПИСАНІ КОНСТРУКТОРИ, ГЕТТЕРИ ТА СЕТТЕРИ.
    // Lombok(@Data, @NoArgsConstructor, @AllArgsConstructor) зробить це за вас.
    // Якщо ви залишите їх, вони можуть конфліктувати з Lombok.
}
package com.guessthewordapp.domain.enteties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * Сутність, що представляє статистику для слова.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WordStats {
    private Long id; // Відповідає stat_id
    private Long wordId; // Відповідає word_id
    private int correctCount; // Відповідає correct_count
    private int totalCount; // Відповідає total_count

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordStats that = (WordStats) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

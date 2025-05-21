package com.guessthewordapp.domain.enteties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * Сутність, що представляє ігрову сесію.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GameSession {
    private Long id; // Відповідає session_id
    private Long userId; // Відповідає user_id
    private Timestamp startedAt; // Відповідає start_time
    private Timestamp endedAt; // Відповідає end_time

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameSession that = (GameSession) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

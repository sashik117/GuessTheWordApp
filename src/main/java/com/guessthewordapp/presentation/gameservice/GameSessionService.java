package com.guessthewordapp.presentation.gameservice;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class GameSessionService {
    private final Map<Long, Deque<GameSessionStats>> userSessions = new HashMap<>();
    private static final int MAX_HISTORY = 10;

    public void saveSession(Long userId, GameSessionStats session) {
        Deque<GameSessionStats> sessions = userSessions.computeIfAbsent(userId, k -> new ArrayDeque<>());
        sessions.addFirst(session);
        if (sessions.size() > MAX_HISTORY) sessions.removeLast();
    }

    public List<GameSessionStats> getSessions(Long userId) {
        return new ArrayList<>(userSessions.getOrDefault(userId, new ArrayDeque<>()));
    }
}


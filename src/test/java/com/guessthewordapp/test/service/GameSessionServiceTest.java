package com.guessthewordapp.test.service;

import com.guessthewordapp.application.contract.GameSessionService;
import com.guessthewordapp.application.contract.dto.GameSessionDTO;
import com.guessthewordapp.application.impl.GameSessionServiceImpl;
import com.guessthewordapp.domain.enteties.GameSession;
import com.guessthewordapp.infrastructure.persistence.contract.GameSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GameSessionServiceTest {

    @Mock
    private GameSessionRepository gameSessionRepository;

    @InjectMocks
    private GameSessionServiceImpl gameSessionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testStartSession() {
        // Підготовка моків
        GameSession mockSession = new GameSession();
        mockSession.setId(1L);
        mockSession.setUserId(1L);
        mockSession.setStartedAt(new Timestamp(System.currentTimeMillis()));

        when(gameSessionRepository.save(any(GameSession.class))).thenReturn(mockSession);

        // Виконання методу, який тестуємо
        GameSessionDTO result = gameSessionService.startSession(1L);

        // Перевірка результатів
        assertNotNull(result, "Результат не повинен бути null");
        assertEquals(1L, result.id(), "ID сесії має співпадати");
        assertEquals(1L, result.userId(), "ID користувача має співпадати");
        assertNotNull(result.startedAt(), "Дата початку має бути заповнена");
        assertNull(result.endedAt(), "Дата завершення має бути null для нової сесії");

        // Перевірка викликів
        verify(gameSessionRepository, times(1)).save(any(GameSession.class));
    }

    @Test
    void testEndSession() {
        // Підготовка моків
        GameSession existingSession = new GameSession();
        existingSession.setId(1L);
        existingSession.setUserId(1L);
        existingSession.setStartedAt(new Timestamp(System.currentTimeMillis()));

        when(gameSessionRepository.findById(1L)).thenReturn(Optional.of(existingSession));
        when(gameSessionRepository.save(any(GameSession.class))).thenReturn(existingSession);

        // Виконання методу, який тестуємо
        GameSessionDTO result = gameSessionService.endSession(1L);

        // Перевірка результатів
        assertNotNull(result.endedAt(), "Дата завершення має бути заповнена");
        assertEquals(1L, result.id(), "ID сесії має співпадати");
        assertEquals(1L, result.userId(), "ID користувача має співпадати");

        // Перевірка викликів
        verify(gameSessionRepository, times(1)).findById(1L);
        verify(gameSessionRepository, times(1)).save(any(GameSession.class));
    }

    @Test
    void testEndSessionNotFound() {
        // Підготовка моків
        when(gameSessionRepository.findById(1L)).thenReturn(Optional.empty());

        // Виконання та перевірка винятку
        assertThrows(IllegalArgumentException.class, () -> {
            gameSessionService.endSession(1L);
        }, "Повинен бути викинутий виняток, якщо сесія не знайдена");
    }

    @Test
    void testGetSessionsByUser() {
        // Підготовка моків
        GameSession mockSession = new GameSession();
        mockSession.setId(1L);
        mockSession.setUserId(1L);
        mockSession.setStartedAt(new Timestamp(System.currentTimeMillis()));

        when(gameSessionRepository.findByUserId(1L)).thenReturn(Collections.singletonList(mockSession));

        // Виконання методу, який тестуємо
        List<GameSessionDTO> result = gameSessionService.getSessionsByUser(1L);

        // Перевірка результатів
        assertFalse(result.isEmpty(), "Список не повинен бути пустим");
        assertEquals(1, result.size(), "Повинен бути один елемент у списку");
        assertEquals(1L, result.get(0).id(), "ID сесії має співпадати");
        assertEquals(1L, result.get(0).userId(), "ID користувача має співпадати");
        assertNotNull(result.get(0).startedAt(), "Дата початку має бути заповнена");
        assertNull(result.get(0).endedAt(), "Дата завершення має бути null");
    }
}
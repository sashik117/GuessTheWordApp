package com.guessthewordapp.test.service;

import com.guessthewordapp.application.contract.WordStatsService;
import com.guessthewordapp.application.contract.dto.WordStatsDTO;
import com.guessthewordapp.application.impl.WordStatsServiceImpl;
import com.guessthewordapp.domain.enteties.User;
import com.guessthewordapp.domain.enteties.WordStats;
import com.guessthewordapp.domain.enums.UserRole;
import com.guessthewordapp.domain.exception.ForbiddenOperationException;
import com.guessthewordapp.infrastructure.persistence.contract.UserRepository;
import com.guessthewordapp.infrastructure.persistence.contract.WordStatsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WordStatsServiceTest {

    @Mock
    private WordStatsRepository wordStatsRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WordStatsServiceImpl wordStatsService;

    @Test
    void testSaveWordStatsAsEditor() {
        User editor = new User();
        editor.setId(1L);
        editor.setRole(UserRole.EDITOR);

        WordStatsDTO dto = new WordStatsDTO(null, 1L, 5, 10);
        WordStats savedStats = new WordStats(1L, 1L, 5, 10);

        when(userRepository.findById(1L)).thenReturn(Optional.of(editor));
        when(wordStatsRepository.save(any(WordStats.class))).thenReturn(savedStats);

        WordStatsDTO result = wordStatsService.saveWordStats(dto, 1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(5, result.correctCount());
        verify(wordStatsRepository, times(1)).save(any(WordStats.class));
    }

    @Test
    void testSaveWordStatsAsPlayerShouldFail() {
        User player = new User();
        player.setId(2L);
        player.setRole(UserRole.PLAYER);

        WordStatsDTO dto = new WordStatsDTO(null, 1L, 5, 10);

        when(userRepository.findById(2L)).thenReturn(Optional.of(player));

        assertThrows(ForbiddenOperationException.class, () -> {
            wordStatsService.saveWordStats(dto, 2L);
        });
    }

    @Test
    void testGetWordStatsByWordId() {
        User player = new User();
        player.setId(1L);
        player.setRole(UserRole.PLAYER);

        WordStats stats = new WordStats(1L, 1L, 3, 7);

        when(userRepository.findById(1L)).thenReturn(Optional.of(player));
        when(wordStatsRepository.findByWordId(1L)).thenReturn(Optional.of(stats));

        Optional<WordStatsDTO> result = wordStatsService.getWordStatsByWordId(1L, 1L);

        assertTrue(result.isPresent());
        assertEquals(3, result.get().correctCount());
        assertEquals(7, result.get().totalCount());
    }

    @Test
    void testIncrementStatsCorrect() {
        User player = new User();
        player.setId(1L);
        player.setRole(UserRole.PLAYER);

        WordStats stats = new WordStats();
        stats.setWordId(1L);
        stats.setCorrectCount(2);
        stats.setTotalCount(5);

        when(userRepository.findById(1L)).thenReturn(Optional.of(player));
        when(wordStatsRepository.findByWordId(1L)).thenReturn(Optional.of(stats));
        when(wordStatsRepository.save(any(WordStats.class))).thenAnswer(inv -> inv.getArgument(0));

        wordStatsService.incrementStats(1L, true, 1L);

        assertEquals(3, stats.getCorrectCount());
        assertEquals(6, stats.getTotalCount());
    }

    @Test
    void testIncrementStatsIncorrect() {
        User player = new User();
        player.setId(1L);
        player.setRole(UserRole.PLAYER);

        WordStats stats = new WordStats();
        stats.setWordId(1L);
        stats.setCorrectCount(2);
        stats.setTotalCount(5);

        when(userRepository.findById(1L)).thenReturn(Optional.of(player));
        when(wordStatsRepository.findByWordId(1L)).thenReturn(Optional.of(stats));
        when(wordStatsRepository.save(any(WordStats.class))).thenAnswer(inv -> inv.getArgument(0));

        wordStatsService.incrementStats(1L, false, 1L);

        assertEquals(2, stats.getCorrectCount());
        assertEquals(6, stats.getTotalCount());
    }

    @Test
    void testIncrementStatsNewWord() {
        User player = new User();
        player.setId(1L);
        player.setRole(UserRole.PLAYER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(player));
        when(wordStatsRepository.findByWordId(1L)).thenReturn(Optional.empty());
        when(wordStatsRepository.save(any(WordStats.class))).thenAnswer(inv -> inv.getArgument(0));

        wordStatsService.incrementStats(1L, true, 1L);

        verify(wordStatsRepository, times(1)).save(any(WordStats.class));
    }
}
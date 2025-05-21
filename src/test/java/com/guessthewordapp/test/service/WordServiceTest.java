package com.guessthewordapp.test.service;

import com.guessthewordapp.application.contract.WordService;
import com.guessthewordapp.application.contract.dto.WordDTO;
import com.guessthewordapp.application.impl.WordServiceImpl;
import com.guessthewordapp.domain.enteties.Word;
import com.guessthewordapp.infrastructure.persistence.contract.WordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WordServiceTest {

    @Mock
    private WordRepository wordRepository;

    @InjectMocks
    private WordServiceImpl wordService;

    @Test
    void testSaveWord() {
        WordDTO dto = new WordDTO(null, "яблуко", 1, "uk", "Фрукт");
        Word savedWord = new Word("яблуко", 1, "uk");
        savedWord.setId(1);
        savedWord.setDescription("Фрукт");

        when(wordRepository.save(any(Word.class))).thenReturn(savedWord);

        WordDTO result = wordService.saveWord(dto);

        assertNotNull(result.id());
        assertEquals("яблуко", result.text());
        assertEquals("Фрукт", result.description());
        verify(wordRepository, times(1)).save(any(Word.class));
    }

    @Test
    void testGetWordById() {
        Word word = new Word("яблуко", 1, "uk");
        word.setId(1);
        word.setDescription("Фрукт");

        when(wordRepository.findById(1)).thenReturn(Optional.of(word));

        Optional<WordDTO> result = wordService.getWordById(1);

        assertTrue(result.isPresent());
        assertEquals("яблуко", result.get().text());
        verify(wordRepository, times(1)).findById(1);
    }

    @Test
    void testGetWordsByLanguage() {
        Word word1 = new Word("яблуко", 1, "uk");
        word1.setId(1);
        Word word2 = new Word("сонце", 1, "uk");
        word2.setId(2);

        when(wordRepository.findByLanguage("uk")).thenReturn(List.of(word1, word2));

        List<WordDTO> results = wordService.getWordsByLanguage("uk");

        assertEquals(2, results.size());
        assertEquals("яблуко", results.get(0).text());
        assertEquals("сонце", results.get(1).text());
        verify(wordRepository, times(1)).findByLanguage("uk");
    }

    @Test
    void testDeleteWord() {
        doNothing().when(wordRepository).delete(1);

        wordService.deleteWord(1);

        verify(wordRepository, times(1)).delete(1);
    }
}
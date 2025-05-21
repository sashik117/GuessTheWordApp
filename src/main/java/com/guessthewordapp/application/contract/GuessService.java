package com.guessthewordapp.application.contract;

import com.guessthewordapp.application.contract.dto.GuessDTO;
import java.util.List;

public interface GuessService {
    GuessDTO saveGuess(GuessDTO guessDTO);
    List<GuessDTO> getGuessesForSession(Long sessionId);
}
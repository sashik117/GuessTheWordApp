package com.guessthewordapp.application.contract;

import com.guessthewordapp.application.contract.dto.GameSessionDTO;
import java.util.List;

public interface GameSessionService {
    GameSessionDTO startSession(Long userId);
    GameSessionDTO endSession(Long sessionId);
    List<GameSessionDTO> getSessionsByUser(Long userId);
}
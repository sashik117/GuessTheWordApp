package com.guessthewordapp.application.impl;

import com.guessthewordapp.application.contract.GameSessionService;
import com.guessthewordapp.application.contract.dto.GameSessionDTO;
import com.guessthewordapp.domain.enteties.GameSession;
import com.guessthewordapp.infrastructure.persistence.contract.GameSessionRepository;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("appGameSessionService")
public class GameSessionServiceImpl implements GameSessionService {
    private final GameSessionRepository gameSessionRepository;

    public GameSessionServiceImpl(GameSessionRepository gameSessionRepository) {
        this.gameSessionRepository = gameSessionRepository;
    }

    @Override
    public GameSessionDTO startSession(Long userId) {
        GameSession session = new GameSession();
        session.setUserId(userId);
        session.setStartedAt(new Timestamp(System.currentTimeMillis()));
        gameSessionRepository.save(session);
        return mapToDTO(session);
    }

    @Override
    public GameSessionDTO endSession(Long sessionId) {
        Optional<GameSession> sessionOpt = gameSessionRepository.findById(sessionId);
        if (sessionOpt.isPresent()) {
            GameSession session = sessionOpt.get();
            session.setEndedAt(new Timestamp(System.currentTimeMillis()));
            gameSessionRepository.save(session);
        } else {
            throw new IllegalArgumentException("Session not found");
        }
        return null;
    }

    @Override
    public List<GameSessionDTO> getSessionsByUser(Long userId) {
        List<GameSession> sessions = gameSessionRepository.findByUserId(userId);
        return sessions.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private GameSessionDTO mapToDTO(GameSession session) {
        return new GameSessionDTO(
            session.getId(),
            session.getUserId(),
            session.getStartedAt(),
            session.getEndedAt()
        );
    }
}

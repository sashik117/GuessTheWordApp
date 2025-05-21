package com.guessthewordapp.infrastructure.persistence.contract;

import com.guessthewordapp.domain.enteties.GameSession;

import com.guessthewordapp.infrastructure.persistence.Repository;
import java.util.List;

public interface GameSessionRepository extends Repository<GameSession, Long> {
    List<GameSession> findByUserId(Long userId);

    @Override
    GameSession update(Long id, GameSession entity);

    @Override
    void delete(Long id);

    @Override
    Object extractId(Object entity);
}

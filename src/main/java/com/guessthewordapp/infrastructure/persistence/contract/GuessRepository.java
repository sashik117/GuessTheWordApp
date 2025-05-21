package com.guessthewordapp.infrastructure.persistence.contract;

import com.guessthewordapp.domain.enteties.Guess;
import com.guessthewordapp.infrastructure.persistence.Repository;

import java.util.List;

public interface GuessRepository extends Repository<Guess, Long> {
    List<Guess> findBySessionId(Long sessionId);

    @Override
    Guess update(Long id, Guess entity);

    @Override
    void delete(Long id);

    @Override
    Object extractId(Object entity);
}

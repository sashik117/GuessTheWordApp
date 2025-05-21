package com.guessthewordapp.infrastructure.persistence.contract;

import com.guessthewordapp.domain.enteties.WordStats;

import com.guessthewordapp.infrastructure.persistence.Repository;
import java.util.Optional;

public interface WordStatsRepository extends Repository<WordStats, Long> {
    Optional<WordStats> findByWordId(Long wordId);

    @Override
    WordStats update(Long id, WordStats entity);

    @Override
    void delete(Long id);

    @Override
    Object extractId(Object entity);
}

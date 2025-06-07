package com.guessthewordapp.infrastructure.persistence.contract;

import com.guessthewordapp.domain.enteties.WordStats;
import com.guessthewordapp.infrastructure.persistence.Repository;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public interface WordStatsRepository extends Repository<WordStats, Long> {

    // Знайти статистику за ідентифікатором слова
    Optional<WordStats> findByWordId(Long wordId);

    // Знайти статистику за ідентифікатором слова та користувача
    Optional<WordStats> findByWordIdAndUserId(Long wordId, Long userId);

    // Знайти всю статистику за ідентифікатором користувача
    List<WordStats> findByUserId(Long userId);

    @Override
    WordStats update(Long id, WordStats entity);

    @Override
    void delete(Long id);

    @Override
    Object extractId(Object entity);

    long count(Object filter);

    <R> List<R> groupBy(Object aggregation, Function<ResultSet, R> resultMapper);
}
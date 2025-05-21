package com.guessthewordapp.infrastructure.persistence.contract;

import com.guessthewordapp.domain.enteties.Hint;

import com.guessthewordapp.infrastructure.persistence.Repository;
import java.util.List;

public interface HintRepository extends Repository<Hint, Long> {
    List<Hint> findByWordId(Long wordId);

    @Override
    Hint update(Long id, Hint entity);

    @Override
    void delete(Long id);

    @Override
    Object extractId(Object entity);
}

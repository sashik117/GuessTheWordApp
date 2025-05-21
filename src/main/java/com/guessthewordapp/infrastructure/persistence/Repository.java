package com.guessthewordapp.infrastructure.persistence;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;

/**
 * Інтерфейс для основних операцій з репозиторієм.
 */
public interface Repository<T, ID> {

    /** Фільтр для запитів. */
    @FunctionalInterface
    interface QueryFilter {
        void apply(StringJoiner whereClause, List<Object> parameters);
    }

    /** Агрегація даних. */
    @FunctionalInterface
    interface DataAggregation {
        void apply(StringJoiner selectClause, StringJoiner groupByClause);
    }

    /** Відображення рядка на об'єкт. */
    @FunctionalInterface
    interface RowMapper<R> {
        R map(ResultSet rs);
    }

    Optional<T> findById(ID id);

    List<T> findByField(String fieldName, Object value);

    List<T> findAll(QueryFilter filter, String sortBy, boolean isAscending, int offset, int limit, String baseSql);

    List<T> findAll(QueryFilter filter, String sortBy, boolean isAscending, int offset, int limit);

    List<T> findAll(int offset, int limit);

    List<T> findAll();

    long count(QueryFilter filter);

    long count();

    <R> List<R> groupBy(DataAggregation aggregation, Function<ResultSet, R> resultMapper);

    T save(T entity);

    List<T> saveAll(List<T> entities);

    T update(ID id, T entity);

    Map<ID, T> updateAll(Map<ID, T> entities);

    void delete(ID id);

    void deleteAll(List<ID> ids);

    Object extractId(Object entity);
}

package com.guessthewordapp.config;

import com.guessthewordapp.infrastructure.persistence.contract.*;
import com.guessthewordapp.infrastructure.persistence.impl.*;
import com.guessthewordapp.infrastructure.persistence.util.ConnectionPool;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan({
    "com.guessthewordapp.application.impl",
    "com.guessthewordapp.infrastructure"  // Додали цей пакет
})
public class InfrastructureConfig {

    @Bean
    public ConnectionPool.PoolConfig poolConfig() {
        return new ConnectionPool.PoolConfig.Builder()
            .withUrl("jdbc:sqlite:./guesstheword.db")
            .withMaxConnections(5)
            .build();
    }

    @Bean
    public ConnectionPool connectionPool(ConnectionPool.PoolConfig poolConfig) {
        return new ConnectionPool(poolConfig);
    }

    @Bean
    public GameSessionRepository gameSessionRepository(ConnectionPool connectionPool) {
        return new GameSessionRepositoryImpl(connectionPool);
    }

    @Bean
    public GuessRepository guessRepository(ConnectionPool connectionPool) {
        return new GuessRepositoryImpl(connectionPool);
    }

    @Bean
    public HintRepository hintRepository(ConnectionPool connectionPool) {
        return new HintRepositoryImpl(connectionPool);
    }

    @Bean
    public UserRepository userRepository(ConnectionPool connectionPool) {
        return new UserRepositoryImpl(connectionPool);
    }

    @Bean
    public WordRepository wordRepository(ConnectionPool connectionPool) {
        return new WordRepositoryImpl(connectionPool);
    }

    @Bean
    public WordStatsRepository wordStatsRepository(ConnectionPool connectionPool) {
        return new WordStatsRepositoryImpl(connectionPool);
    }
}
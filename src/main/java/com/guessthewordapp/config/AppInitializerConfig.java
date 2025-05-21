package com.guessthewordapp.config;

import com.guessthewordapp.infrastructure.persistence.util.PersistenceInitializer;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppInitializerConfig {

    private final PersistenceInitializer persistenceInitializer;

    public AppInitializerConfig(PersistenceInitializer persistenceInitializer) {
        this.persistenceInitializer = persistenceInitializer;
    }

    @Bean
    public ApplicationRunner applicationRunner() {
        return args -> {
            try {
                // Ініціалізуємо базу даних при запуску програми
                persistenceInitializer.init();
                System.out.println("База даних успішно ініціалізована.");
            } catch (Exception e) {
                System.err.println("Помилка ініціалізації бази даних: " + e.getMessage());
            }
        };
    }
}

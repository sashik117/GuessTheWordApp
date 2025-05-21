/*package com.guessthewordapp;

import com.guessthewordapp.config.DatabaseInitializer;
import com.guessthewordapp.domain.enteties.Word;
import com.guessthewordapp.infrastructure.persistence.PersistenceContext;
import com.guessthewordapp.infrastructure.persistence.contract.WordRepository;
import com.guessthewordapp.infrastructure.persistence.util.ConnectionPool;
import com.guessthewordapp.infrastructure.persistence.util.PersistenceInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@SpringBootApplication(scanBasePackages = "com.guessthewordapp")
public class MainApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Component
    public static class Application {

        private final PersistenceContext persistenceContext;
        private final WordRepository wordRepository;
        private final PersistenceInitializer persistenceInitializer;
        private final ConnectionPool connectionPool;
        private final DatabaseInitializer databaseInitializer;

        public Application(
            PersistenceContext persistenceContext,
            WordRepository wordRepository,
            PersistenceInitializer persistenceInitializer,
            ConnectionPool connectionPool,
            DatabaseInitializer databaseInitializer) {
            this.persistenceContext = persistenceContext;
            this.wordRepository = wordRepository;
            this.persistenceInitializer = persistenceInitializer;
            this.connectionPool = connectionPool;
            this.databaseInitializer = databaseInitializer;
        }

        @EventListener(ApplicationReadyEvent.class)
        public void run() {
            databaseInitializer.initialize();
            persistenceInitializer.init();

            Word word = new Word("example", 1, null);  // Створюємо без id
            persistenceContext.registerNew(word);
            persistenceContext.commit();

            System.out.println("\u2705 Слова успішно збережені.");

            connectionPool.shutdown();
        }
    }
}*/
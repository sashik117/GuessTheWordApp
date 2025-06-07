package com.guessthewordapp.config;

import com.guessthewordapp.application.contract.WordService;
import com.guessthewordapp.infrastructure.HashingService;
import com.guessthewordapp.presentation.view.viewmodels.AdminWordViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
@ComponentScan(basePackages = "com.guessthewordapp")
public class AppConfig {

    private static final Logger logger = LoggerFactory.getLogger(AppConfig.class);

    @Bean
    public ResourceBundleMessageSource messageSource() {
        logger.debug("Configuring ResourceBundleMessageSource");
        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
        source.setBasename("messages");
        source.setDefaultEncoding("UTF-8");
        return source;
    }

    @Bean
    public AdminWordViewModel wordAdminViewModel(WordService wordService) {
        logger.debug("Creating AdminWordViewModel bean");
        return new AdminWordViewModel(wordService);
    }

    @Bean
    public HashingService hashingService() {
        logger.debug("Creating HashingService bean");
        return new HashingService();
    }
}
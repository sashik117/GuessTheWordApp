package com.guessthewordapp.infrastructure;

import org.springframework.stereotype.Service;

/**
 * Сервіс-заглушка для відправки email.
 * Просто виводить інформацію у консоль.
 */
@Service  // Додали цю анотацію
public class EmailService {

    /**
     * Метод-заглушка для "відправки" листа.
     *
     * @param to      Email отримувача
     * @param subject Тема листа
     * @param content Текст листа
     */
    public void sendEmail(String to, String subject, String content) {
        System.out.println("📤 Симуляція надсилання email:");
        System.out.println("➡ Кому: " + to);
        System.out.println("📌 Тема: " + subject);
        System.out.println("📝 Зміст: " + content);
    }
}
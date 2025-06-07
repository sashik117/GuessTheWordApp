package com.guessthewordapp.infrastructure;

import com.guessthewordapp.domain.enteties.User;
import com.guessthewordapp.domain.enums.UserRole;
import com.guessthewordapp.infrastructure.persistence.contract.UserRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class RegistrationService {
    private final UserRepository userRepository;
    private final EmailService emailService;

    public RegistrationService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public void register(String username, String email, String password, UserRole role) {
        // Валідація email
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Невалідний email. Введіть коректну адресу");
        }

        // Перевірка на дублікат
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("Цей email уже використовується");
        }

        // Хешування пароля
        String hashedPassword = HashingService.hashPassword(password);
        User newUser = new User(null, username, email, hashedPassword, role);

        // Збереження
        try {
            userRepository.save(newUser);
        } catch (Exception e) {
            throw new RuntimeException("Помилка при збереженні користувача: " + e.getMessage());
        }

        // Відправка email
        try {
            emailService.sendEmail(
                email,
                "Успішна реєстрація",
                "Вітаємо у грі 'Вгадай слово', " + username + "!"
            );
        } catch (Exception e) {
            // Логимо помилку, але не перериваємо реєстрацію
            System.err.println("Failed to send registration email: " + e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        String regex = "^[^\\s@]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email != null && email.matches(regex);
    }
}
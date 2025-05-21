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
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            throw new RuntimeException("Користувач з такою поштою вже існує");
        }

        String hashedPassword = HashingService.hashPassword(password);
        User newUser = new User(null, username, email, hashedPassword, role);

        userRepository.save(newUser);

        emailService.sendEmail(
            email,
            "Успішна реєстрація",
            "Вітаємо у грі 'Вгадай слово', " + username + "!"
        );
    }
}

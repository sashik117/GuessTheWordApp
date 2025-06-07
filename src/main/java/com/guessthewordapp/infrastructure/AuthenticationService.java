package com.guessthewordapp.infrastructure;

import com.guessthewordapp.domain.enteties.User;
import com.guessthewordapp.domain.enums.UserRole;
import com.guessthewordapp.infrastructure.persistence.contract.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserRepository userRepository;
    private final RegistrationService registrationService;

    public AuthenticationService(UserRepository userRepository, RegistrationService registrationService) {
        this.userRepository = userRepository;
        this.registrationService = registrationService;
        logger.debug("AuthenticationService initialized");
    }

    public User login(String email, String password) {
        logger.debug("Login attempt for email: {}", email);
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            logger.warn("No user found: {}", email);
            throw new IllegalArgumentException("Користувача не знайдено");
        }

        User user = userOpt.get();
        String hashedInputPassword = HashingService.hashPassword(password);

        if (!user.getPasswordHash().equals(hashedInputPassword)) {
            logger.warn("Invalid password for user: {}", email);
            throw new IllegalArgumentException("Невірний пароль");
        }

        logger.info("Login successful for email: {}, userId: {}", email, user.getId());
        return user;
    }

    public User register(String username, String password, String email, String role) {
        logger.debug("Registering user: username={}, email={}, role={}", username, email, role);
        registrationService.register(username, email, password, UserRole.valueOf(role));
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new IllegalArgumentException("Користувач не знайдено після реєстрації"));
        logger.info("User registered successfully: username={}, email={}, userId: {}", username, email, user.getId());
        return user;
    }
}
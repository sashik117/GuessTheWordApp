package com.guessthewordapp.infrastructure;

import com.guessthewordapp.domain.enteties.User;
import com.guessthewordapp.domain.enums.UserRole;
import com.guessthewordapp.infrastructure.persistence.contract.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RegistrationService registrationService;

    public AuthenticationService(UserRepository userRepository, RegistrationService registrationService) {
        this.userRepository = userRepository;
        this.registrationService = registrationService;
    }

    public User login(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Користувача не знайдено");
        }

        User user = userOpt.get();
        String hashedPassword = HashingService.hashPassword(password);

        if (!user.getPasswordHash().equals(hashedPassword)) {
            throw new RuntimeException("Невірний пароль");
        }

        return user;
    }

    public User register(String username, String password, String email, UserRole role) {
        registrationService.register(username, email, password, role);
        return userRepository.findByUsername(username).orElseThrow();
    }
}

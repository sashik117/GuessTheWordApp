package com.guessthewordapp.application.impl;

import com.guessthewordapp.application.contract.UserService;
import com.guessthewordapp.application.contract.dto.UserDTO;
import com.guessthewordapp.domain.enteties.User;
import com.guessthewordapp.domain.enums.UserRole;
import com.guessthewordapp.infrastructure.HashingService;
import com.guessthewordapp.infrastructure.persistence.contract.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        logger.debug("UserServiceImpl initialized");
    }

    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        logger.debug("Registering user: email={}", userDTO.email());
        // Перевірка чи користувач з таким email вже існує
        if (userRepository.findByEmail(userDTO.email()).isPresent()) {
            logger.warn("User already exists: email={}", userDTO.email());
            throw new IllegalArgumentException("Користувач з такою поштою вже існує");
        }

        // Хешування пароля
        String hashedPassword = HashingService.hashPassword(userDTO.password());
        logger.debug("Password hashed for user: email={}", userDTO.email());

        // Створення нового користувача
        User user = new User();
        user.setUsername(userDTO.username());
        user.setEmail(userDTO.email());
        user.setPasswordHash(hashedPassword);
        user.setRole(UserRole.valueOf(userDTO.role()));

        // Збереження користувача
        User savedUser = userRepository.save(user);
        logger.info("User registered: id={}, email={}", savedUser.getId(), savedUser.getEmail());

        // Маппінг назад в DTO
        return mapToDTO(savedUser);
    }

    @Override
    public Optional<UserDTO> findUserByEmail(String email) {
        logger.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email)
            .map(this::mapToDTO);
    }

    @Override
    public Optional<UserDTO> findUserById(Long userId) {
        logger.debug("Finding user by id: {}", userId);
        return userRepository.findById(userId)
            .map(this::mapToDTO);
    }

    @Override
    public void updateUserRole(Long userId, String newRole) {
        logger.debug("Updating user role: userId={}, newRole={}", userId, newRole);
        // Знаходимо користувача
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Користувача не знайдено"));

        // Оновлюємо роль
        user.setRole(UserRole.valueOf(newRole));

        // Зберігаємо зміни
        userRepository.save(user);
        logger.info("User role updated: userId={}, newRole={}", userId, newRole);
    }

    private UserDTO mapToDTO(User user) {
        return new UserDTO(
            user.getUsername(),
            user.getEmail(),
            "", // Пароль не повертаємо з безпечних міркувань
            null, // avatar поки не реалізовано
            null, // birthday поки не реалізовано
            user.getRole().name() // Конвертуємо enum в String
        );
    }
}
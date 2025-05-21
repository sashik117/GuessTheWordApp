package com.guessthewordapp.application.impl;

import com.guessthewordapp.application.contract.UserService;
import com.guessthewordapp.application.contract.dto.UserDTO;
import com.guessthewordapp.domain.enteties.User;
import com.guessthewordapp.domain.enums.UserRole;
import com.guessthewordapp.infrastructure.persistence.contract.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        // Перевірка чи користувач з таким email вже існує
        if (userRepository.findByEmail(userDTO.email()).isPresent()) {
            throw new IllegalArgumentException("Користувач з такою поштою вже існує");
        }

        // Хешування пароля (потрібно буде додати HashingService)
        String hashedPassword = userDTO.password(); // Тимчасово, потім замінити на хешування

        // Створення нового користувача
        User user = new User();
        user.setUsername(userDTO.username());
        user.setEmail(userDTO.email());
        user.setPasswordHash(hashedPassword);
        user.setRole(UserRole.valueOf(userDTO.role())); // Конвертуємо String в UserRole

        // Збереження користувача
        User savedUser = userRepository.save(user);

        // Маппінг назад в DTO
        return mapToDTO(savedUser);
    }

    @Override
    public Optional<UserDTO> findUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .map(this::mapToDTO);
    }

    @Override
    public Optional<UserDTO> findUserById(Long userId) {
        return userRepository.findById(userId)
            .map(this::mapToDTO);
    }

    @Override
    public void updateUserRole(Long userId, String newRole) {
        // Знаходимо користувача
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Користувача не знайдено"));

        // Оновлюємо роль
        user.setRole(UserRole.valueOf(newRole));

        // Зберігаємо зміни
        userRepository.save(user);
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
package com.guessthewordapp.test.service;

import com.guessthewordapp.application.contract.UserService;
import com.guessthewordapp.application.contract.dto.UserDTO;
import com.guessthewordapp.application.impl.UserServiceImpl;
import com.guessthewordapp.domain.enteties.User;
import com.guessthewordapp.domain.enums.UserRole;
import com.guessthewordapp.infrastructure.persistence.contract.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testRegisterUser() {
        UserDTO dto = new UserDTO(
            "newuser",
            "new@example.com",
            "password123",
            Path.of("avatar.jpg"),
            LocalDate.of(1990, 1, 1),
            "PLAYER"
        );

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername(dto.username());
        savedUser.setEmail(dto.email());
        savedUser.setPasswordHash("hashed_password");
        savedUser.setRole(UserRole.PLAYER);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserDTO result = userService.registerUser(dto);

        assertNotNull(result);
        assertEquals("newuser", result.username());
        assertEquals("new@example.com", result.email());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testFindUserByEmail() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedpass");
        user.setRole(UserRole.PLAYER);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        Optional<UserDTO> result = userService.findUserByEmail("test@example.com");

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().username());
        assertEquals("test@example.com", result.get().email());
        verify(userRepository, times(1)).findByEmail("test@example.com");
    }

    @Test
    void testFindUserById() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedpass");
        user.setRole(UserRole.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<UserDTO> result = userService.findUserById(1L);

        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().username());
        assertEquals("ADMIN", result.get().role());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateUserRole() {
        doAnswer(invocation -> {
            Long userId = invocation.getArgument(0);
            String role = invocation.getArgument(1);
            assertEquals(1L, userId);
            assertEquals("ADMIN", role);
            return null;
        }).when(userRepository).save(any(User.class));

        userService.updateUserRole(1L, "ADMIN");

        verify(userRepository, times(1)).save(any(User.class));
    }
}

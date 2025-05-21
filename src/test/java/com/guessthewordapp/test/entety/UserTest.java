package com.guessthewordapp.test.entety;

import com.guessthewordapp.domain.enteties.User;
import com.guessthewordapp.domain.enums.UserRole;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserPermissions() {
        User admin = new User();
        admin.setRole(UserRole.ADMIN);

        User moderator = new User();
        moderator.setRole(UserRole.MODERATOR);

        User player = new User();
        player.setRole(UserRole.PLAYER);

        // ADMIN (ordinal=0) >= всі ролі
        assertTrue(admin.hasPermission(UserRole.ADMIN));
        assertTrue(admin.hasPermission(UserRole.MODERATOR));
        assertTrue(admin.hasPermission(UserRole.PLAYER));

        // MODERATOR (ordinal=1) >= MODERATOR і PLAYER
        assertFalse(moderator.hasPermission(UserRole.ADMIN));
        assertTrue(moderator.hasPermission(UserRole.MODERATOR));
        assertTrue(moderator.hasPermission(UserRole.PLAYER));

        // PLAYER (ordinal=2) тільки >= PLAYER
        assertFalse(player.hasPermission(UserRole.ADMIN));
        assertFalse(player.hasPermission(UserRole.MODERATOR));
        assertTrue(player.hasPermission(UserRole.PLAYER));
    }

    @Test
    void testGettersAndSetters() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPasswordHash("hashedpass");
        user.setRole(UserRole.MODERATOR);

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals("hashedpass", user.getPasswordHash());
        assertEquals(UserRole.MODERATOR, user.getRole());
    }

    @Test
    void testAllArgsConstructor() {
        User user = new User(1L, "testuser", "test@example.com", "hashedpass", UserRole.PLAYER);

        assertEquals(1L, user.getId());
        assertEquals("testuser", user.getUsername());
        assertEquals(UserRole.PLAYER, user.getRole());
    }
}
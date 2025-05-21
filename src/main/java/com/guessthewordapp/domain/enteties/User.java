package com.guessthewordapp.domain.enteties;

import com.guessthewordapp.domain.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private UserRole role;

    public boolean hasPermission(UserRole requiredRole) {
        if (this.role == null) return false;

        switch (this.role) {
            case ADMIN: return true;
            case MODERATOR: return requiredRole != UserRole.ADMIN;
            case PLAYER: return requiredRole == UserRole.PLAYER;
            default: return false;
        }
    }
}
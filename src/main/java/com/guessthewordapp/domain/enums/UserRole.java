package com.guessthewordapp.domain.enums;

public enum UserRole {
    PLAYER,
    EDITOR,
    ADMIN,
    MODERATOR;

    public boolean isAtLeast(UserRole requiredRole) {
        return this.ordinal() >= requiredRole.ordinal();
    }
}

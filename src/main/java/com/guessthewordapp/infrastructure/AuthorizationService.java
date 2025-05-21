package com.guessthewordapp.infrastructure;

import com.guessthewordapp.domain.enteties.User;
import com.guessthewordapp.domain.enums.UserRole;
import org.springframework.stereotype.Service;

@Service
public class AuthorizationService {
    public boolean hasRole(User user, UserRole requiredRole) {
        if (user == null || user.getRole() == null) return false;
        return user.getRole() == requiredRole;
    }
}
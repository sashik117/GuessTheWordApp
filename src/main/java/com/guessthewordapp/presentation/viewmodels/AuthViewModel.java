package com.guessthewordapp.presentation.viewmodels;

import com.guessthewordapp.domain.enteties.User;
import com.guessthewordapp.domain.enums.UserRole;
import com.guessthewordapp.infrastructure.AuthenticationService;
import com.guessthewordapp.infrastructure.RegistrationService;

public class AuthViewModel {
    private final AuthenticationService authenticationService;
    private final RegistrationService registrationService;

    public AuthViewModel(AuthenticationService authenticationService, RegistrationService registrationService) {
        this.authenticationService = authenticationService;
        this.registrationService = registrationService;
    }

    public User login(String username, String password) {
        return authenticationService.login(username, password);
    }

    public void register(String username, String email, String password) {
        registrationService.register(username, email, password, UserRole.PLAYER);
    }
}

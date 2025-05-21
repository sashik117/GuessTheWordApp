package com.guessthewordapp.application.contract;

import com.guessthewordapp.application.contract.dto.UserDTO;
import java.util.Optional;

public interface UserService {
    UserDTO registerUser(UserDTO userDTO);
    Optional<UserDTO> findUserByEmail(String email);
    Optional<UserDTO> findUserById(Long userId);
    void updateUserRole(Long userId, String newRole);
}
package com.reza.hatex.services;

import com.reza.hatex.dto.UserDTO;
import com.reza.hatex.entities.User;

public interface UserService {

    void registerUser(UserDTO userDTO);

    void sendVerificationEmail(User user);

    boolean verifyUser(String token);

}

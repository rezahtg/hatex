package com.reza.hatex.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {

    @Column(nullable = false)
    private String usernameOrEmail;
    @Column(nullable = false)
    private String password;

}

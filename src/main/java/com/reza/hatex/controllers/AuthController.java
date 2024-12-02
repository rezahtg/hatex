package com.reza.hatex.controllers;

import com.reza.hatex.dto.LoginDTO;
import com.reza.hatex.dto.UserDTO;
import com.reza.hatex.global.ApiResponse;
import com.reza.hatex.services.AuthenticationService;
import com.reza.hatex.services.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private UserService userService;
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody UserDTO userDTO) {
        try {
            userService.registerUser(userDTO);
            return ResponseEntity.ok(new ApiResponse<>(true, "Registration successful. Please check your email for verification.", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Registration failed: " + e.getMessage(), null));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<String>> verifyEmail(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        boolean isVerified = userService.verifyUser(token);
        return isVerified ? ResponseEntity.ok(new ApiResponse<>(true, "Email verified successfully.", null)):
                ResponseEntity.badRequest().body(new ApiResponse<>(false, "Invalid or expired token.", null));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, String>>> login(@RequestBody LoginDTO loginDTO) {
        Map<String, String> tokens = authenticationService.authenticateUser(loginDTO.getUsernameOrEmail(), loginDTO.getPassword());
        return ResponseEntity.ok(new ApiResponse<>(true, "Login Successful", tokens));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<String>> refreshToken(@RequestBody Map<String, String> refreshTokenRequest) {
        String refreshToken = refreshTokenRequest.get("refreshToken");
        String newAccessToken = authenticationService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(new ApiResponse<>(true, "Refresh Token Successful", newAccessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String token) {
        String cleanedToken = token.substring(7);
        authenticationService.logoutUser(cleanedToken);
        return ResponseEntity.ok(new ApiResponse<>(true, "Logout Successful", null));
    }

}

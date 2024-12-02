package com.reza.hatex.services;

import com.reza.hatex.entities.User;
import com.reza.hatex.repositories.UserRepository;
import com.reza.hatex.utils.JwtUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@Slf4j
public class AuthenticationService {

    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private JwtUtils jwtUtils;
    private StringRedisTemplate redisTemplate;

    public Map<String, String> authenticateUser(String usernameOrEmail, String password) {
        //Authenticating using spring security
        Authentication authentication = authenticationManager.authenticate(
         new UsernamePasswordAuthenticationToken(usernameOrEmail, password)
        );
        //retrieve user after authentication
        User user = (User) authentication.getPrincipal();

        //generate jwt token
        String accessToken = jwtUtils.generateAccessToken(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);

        //store token in redis with expiration time
        redisTemplate.opsForValue().set(refreshToken, user.getUsername(), jwtUtils.getRefreshTokenExpiration(), TimeUnit.MILLISECONDS);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("refreshToken", refreshToken);
        return tokens;
    }

    public String refreshAccessToken(String refreshToken) {
        if (!redisTemplate.hasKey(refreshToken) || !jwtUtils.validateToken(refreshToken)) {
            throw new RuntimeException("invalid or expired refresh token");
        }
        String username = jwtUtils.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new RuntimeException("User not found")
        );

        return jwtUtils.generateAccessToken(user);
    }

    public void logoutUser(String refreshToken) {
        redisTemplate.delete(refreshToken); // Remove the refresh token from Redis on logout
    }

}

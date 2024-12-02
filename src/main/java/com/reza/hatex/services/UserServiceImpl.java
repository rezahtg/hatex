package com.reza.hatex.services;

import com.reza.hatex.dto.UserDTO;
import com.reza.hatex.entities.EmailNotification;
import com.reza.hatex.entities.User;
import com.reza.hatex.repositories.UserRepository;
import com.reza.hatex.utils.EmailNotificationProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailNotificationProducer emailNotificationProducer;

    @Override
    public void registerUser(UserDTO userDTO) {
        User user = User.builder()
                .username(userDTO.getUsername())
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .verifiedToken(UUID.randomUUID().toString())
                .tokenExpiry(LocalDateTime.now().plusMinutes(30))
                .build();
        log.info("Save user to DB:: user details : {}", user);
        userRepository.save(user);
        log.info("Sent Email Verification user to DB:: to username : {}", user.getUsername());
        sendVerificationEmail(user);
    }

    @Override
    public void sendVerificationEmail(User user) {
        EmailNotification emailNotification = new EmailNotification();
        emailNotification.setTo(user.getEmail());
        emailNotification.setSubject("Email Verification");
        emailNotification.setBody("Click the link to verify your email: http://localhost:8080/api/auth/verify?token=" + user.getVerifiedToken());
        emailNotificationProducer.sendEmailNotification(emailNotification);
    }

    @Override
    public boolean verifyUser(String token) {
        Optional<User> optionalUser = userRepository.findByVerifiedToken(token);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
                return false;
            }
            user.setVerified(true);
            user.setVerifiedToken(null);
            user.setTokenExpiry(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }
}

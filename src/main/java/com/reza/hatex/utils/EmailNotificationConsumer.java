package com.reza.hatex.utils;

import com.reza.hatex.entities.EmailNotification;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@Data
@AllArgsConstructor
public class EmailNotificationConsumer {
    private static final int retryCount = 0;
    private static final boolean success = false;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_BACKOFF_MS = 2000; // 2 seconds
    private RabbitTemplate rabbitTemplate;

    private JavaMailSender mailSender;

    @RabbitListener(queues = "emailNotifications")
    public void receiveEmailNotification(EmailNotification emailNotification) {
        log.info("Received email notification for: {}", emailNotification.getTo());
        int attempt = 0;

        while (attempt < MAX_RETRIES) {
            try {
                sendEmail(emailNotification);
                return; // Exit on successful send
            } catch (Exception e) {
                attempt++;
                logError(attempt, emailNotification, e);

                if (attempt < MAX_RETRIES) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(RETRY_BACKOFF_MS * attempt); // Exponential backoff
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt(); // Restore interrupt status
                    }
                }
            }
        }

        sendToDeadLetterQueue(emailNotification);
    }

    public void sendEmail(EmailNotification emailNotification) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailNotification.getTo());
        message.setSubject(emailNotification.getSubject());
        message.setText(emailNotification.getBody());
        mailSender.send(message);
        log.info("Email sent to {}", emailNotification.getTo());
    }

    private void sendToDeadLetterQueue(EmailNotification emailNotification) {
        // Log the error details
        log.error("Sending email notification to DLQ: {}", emailNotification);

        // Optionally, send to a different failure queue for further processing
        // Assuming you have a failure queue defined
        try {
            rabbitTemplate.convertAndSend("emailNotifications.failure", emailNotification);
            log.info("Email notification successfully sent to failure queue: {}", emailNotification);
        } catch (Exception e) {
            log.error("Failed to send email notification to failure queue: {}", e.getMessage());
        }
    }

    private void logError(int attempt, EmailNotification emailNotification, Exception e) {
        log.error("Failed to send email to {}, attempt {}/{}. Error: {}", emailNotification.getTo(), attempt, MAX_RETRIES, e.getMessage());
    }

}

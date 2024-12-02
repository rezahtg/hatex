package com.reza.hatex.utils;

import com.reza.hatex.entities.EmailNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class FailureQueueConsumer {

    @RabbitListener(queues = "emailNotifications.failure")
    public void handleFailureNotification(EmailNotification emailNotification) {
        // Log the details of the failed email notification
        log.warn("Handling failed email notification from failure queue: {}", emailNotification);
        log.error("Failed to send email to: {} | Subject: {} | Body: {}",
                emailNotification.getTo(),
                emailNotification.getSubject(),
                emailNotification.getBody());

        // Further processing can go here
    }

}

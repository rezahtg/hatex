package com.reza.hatex.utils;

import com.reza.hatex.entities.EmailNotification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DeadLetterConsumer {

    @RabbitListener(queues = "emailNotifications.dlq")
    public void handleDeadLetterNotification(EmailNotification emailNotification) {
        log.warn("Handling dead-letter email notification for: {}", emailNotification);
        // Implement further processing logic (e.g., alerting, logging)
    }

}

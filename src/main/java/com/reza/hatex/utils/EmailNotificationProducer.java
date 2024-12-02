package com.reza.hatex.utils;

import com.reza.hatex.entities.EmailNotification;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
@AllArgsConstructor
@Slf4j
public class EmailNotificationProducer {

    private RabbitTemplate rabbitTemplate;
    private final String queueName = "emailNotifications";
    private final ConcurrentHashMap<String, Instant> lastSentMap = new ConcurrentHashMap<>();
    private final long rateLimitMs = 10000; // 5 seconds


    public void sendEmailNotification(EmailNotification emailNotification) {
        Instant now = Instant.now();
        Instant lastSent = lastSentMap.get(emailNotification.getTo());

        if (lastSent != null && now.isBefore(lastSent.plusMillis(rateLimitMs))) {
            log.warn("Rate limit exceeded for email: {}", emailNotification.getTo());
            return; // Skip sending to respect rate limit
        }

        lastSentMap.put(emailNotification.getTo(), now);
        rabbitTemplate.convertAndSend(queueName, emailNotification, message -> {
            message.getMessageProperties().setPriority(1);
            return message;
        });
    }

}

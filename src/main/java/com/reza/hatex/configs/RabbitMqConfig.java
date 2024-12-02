package com.reza.hatex.configs;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    @Bean
    public Queue emailNotificationQueue() {
        return QueueBuilder.durable("emailNotifications")
                .withArgument("x-dead-letter-exchange", "emailNotifications.dlx")
                .withArgument("x-dead-letter-routing-key", "emailNotifications.dlq")
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue("emailNotifications.dlq");
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange("emailNotifications.dlx");
    }

    @Bean
    public Binding deadLetterBinding() {
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with("emailNotifications.dlq");
    }

    @Bean
    public Queue failureQueue() {
        return new Queue("emailNotifications.failure");
    }

    @Bean
    public Binding failureQueueBinding() {
        return BindingBuilder.bind(failureQueue())
                .to(deadLetterExchange()) // Reuse the DLX for binding
                .with("emailNotifications.failure");
    }


    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }


}

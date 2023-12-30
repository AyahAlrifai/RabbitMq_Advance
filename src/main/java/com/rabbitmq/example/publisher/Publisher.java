package com.rabbitmq.example.publisher;

import com.rabbitmq.example.configuration.RabbitMQConfig;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Publisher.
 *
 * @author Ayah Refai
 * @since 12/19/2023
 */
@Component
public class Publisher {

    private final AmqpTemplate rabbitTemplate;

    @Autowired
    public Publisher(final AmqpTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendDirectMessage(final String message, final String routingKey, final Integer expiration) {
        if (expiration == 0) {
            rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, routingKey, message, msg -> {
                msg.getMessageProperties().setExpiration(String.valueOf(expiration));
                return msg;
            });
        } else {
            rabbitTemplate.convertAndSend(RabbitMQConfig.DIRECT_EXCHANGE, routingKey, message);
        }
        System.out.println("Message: " + message + " RoutingKey: " + routingKey);
    }
}

package com.rabbitmq.example.controller;

import com.rabbitmq.example.publisher.Publisher;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RabbitMQController.
 *
 * @author Ayah Refai
 * @since 12/19/2023
 */
@RestController
public class RabbitMQController {

    public final Publisher publisher;

    @Autowired
    public RabbitMQController(Publisher publisher) {
        this.publisher = publisher;
    }

    @GetMapping("/direct-exchange")
    public void directExchange(@PathParam("message") final String message,
                               @PathParam("routingKey") final String routingKey,
                               @PathParam("expiration") final Integer expiration) {
        this.publisher.sendDirectMessage(message, routingKey, expiration);
    }
}

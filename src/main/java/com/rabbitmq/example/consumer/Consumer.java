package com.rabbitmq.example.consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.example.configuration.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Consumer.
 *
 * @author Ayah Refai
 * @since 12/19/2023
 */
@Component
public class Consumer {

    /**
     * Listening to q1.
     *
     * @param message message
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_1, containerFactory = "rabbitListenerPrefetchFactory")
    public void receiveMessage1(final String message) throws InterruptedException {
        System.out.println("q1 ------------> Received Message: " + message);
        System.out.println("q1 ------------> Waiting 4000ms");
        Thread.sleep(4000L);
    }

    /**
     * Listening to q2.
     *
     * @param message message
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_2, containerFactory = "rabbitListenerPrefetchFactory")
    public void receiveMessage2(final String message) throws InterruptedException {
        System.out.println("q2 ------------> Received Message: " + message);
        System.out.println("q2 ------------> Waiting 4000ms");
        Thread.sleep(4000L);
    }

    /**
     * Listening to q3.
     *
     * @param message message
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_3, ackMode = "MANUAL", containerFactory = "")
    public void receiveMessage3(final String message,
                                final Channel channel,
                                final @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        System.out.println("q3 ------------> Received Message: " + message);
        channel.basicAck(tag, false);
        System.out.println("q3 ------------> Positive ACK ");
    }

    /**
     * Listening to q4.
     *
     * @param message message
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_4, ackMode = "MANUAL", containerFactory = "")
    public void receiveMessage4(final String message,
                                Channel channel,
                                final @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        System.out.println("q4 ------------> Received Message: " + message);
        if (message.equals("requeue")) {
            System.out.println("q4 ------------> Negative ACK, requeue message");
            channel.basicReject(tag, true);
        } else if (message.equals("don't requeue")) {
            System.out.println("q4 ------------> Negative ACK, don't requeue message");
            channel.basicReject(tag, false);
        }
    }

    /**
     * Listening to q5.
     *
     * @param message message
     */
    @RabbitListener(queues = RabbitMQConfig.QUEUE_5, containerFactory = "rabbitListenerPrefetchFactory")
    public void receiveMessage5(final String message) throws InterruptedException {
        System.out.println("q5 ------------> Received Message: " + message);
        System.out.println("q5 ------------> Waiting 10000ms");
        Thread.sleep(10000L);
    }

    /**
     * Listening to dead.letter.queue.
     *
     * @param message message
     */
    @RabbitListener(queues = RabbitMQConfig.DEAD_LETTER_QUEUE, containerFactory = "")
    public void receiveMessageDeadLetter(final String message) {
        System.out.println("deadLetter ------------> Received Message: " + message);
    }
}

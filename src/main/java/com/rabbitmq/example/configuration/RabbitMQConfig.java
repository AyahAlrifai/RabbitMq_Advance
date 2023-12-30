package com.rabbitmq.example.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RabbitMq Configuration.
 *
 * @author Ayah Refai
 * @since 12/19/2023
 */
@Configuration
public class RabbitMQConfig {

    public static final String DIRECT_EXCHANGE = "ayah.direct.exchange";
    public static final String QUEUE_1 = "q1";
    public static final String QUEUE_2 = "q2";
    public static final String QUEUE_3 = "q3";
    public static final String QUEUE_4 = "q4";
    public static final String QUEUE_5 = "q5";
    public static final String DEAD_LETTER_QUEUE = "dead.letter.queue";
    public static final String DEAD_LETTER_ROUTING_KEY = "dead_letter";
    private static final String DEAD_LETTER_EXCHANGE = "ayah.dead.letter.exchange";

    /**
     * direct binding to q1 when routing key is `log1`.
     * direct binding to q2 when routing key is `log2`.
     * direct binding to dead.letter.queue when routing key is `dead_letter`.
     * where exchange is `ayah.direct.exchange`.
     *
     * @return Binding
     */
    private static List<Binding> directBinding() {
        ArrayList<Binding> bindings = new ArrayList<>();
        bindings.add(new Binding(QUEUE_1,
                Binding.DestinationType.QUEUE,
                DIRECT_EXCHANGE,
                "log1",
                null));
        bindings.add(new Binding(QUEUE_2,
                Binding.DestinationType.QUEUE,
                DIRECT_EXCHANGE,
                "log2",
                null));
        bindings.add(new Binding(QUEUE_3,
                Binding.DestinationType.QUEUE,
                DIRECT_EXCHANGE,
                "log3",
                null));
        bindings.add(new Binding(QUEUE_4,
                Binding.DestinationType.QUEUE,
                DIRECT_EXCHANGE,
                "log4",
                null));
        bindings.add(new Binding(QUEUE_5,
                Binding.DestinationType.QUEUE,
                DIRECT_EXCHANGE,
                "log5",
                null));
        bindings.add(new Binding(DEAD_LETTER_QUEUE,
                Binding.DestinationType.QUEUE,
                DEAD_LETTER_EXCHANGE,
                DEAD_LETTER_ROUTING_KEY,
                null));
        return bindings;
    }

    @Bean
    public Declarables topicExchangeBindings() {
        ArrayList<Declarable> declarable = new ArrayList<>();
        //********************************************* Add Queue ***************************************//
        Map<String, Object> arguments1 = new HashMap<>();
        arguments1.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        arguments1.put("x-dead-letter-routing-key", DEAD_LETTER_ROUTING_KEY);
        arguments1.put("x-message-ttl", 3000);
        declarable.add(new Queue(QUEUE_1, true, false, false, arguments1));

        Map<String, Object> arguments2 = new HashMap<>();
        arguments2.put("x-message-ttl", 3000);
        declarable.add(new Queue(QUEUE_2, true, false, false, arguments2));

        declarable.add(new Queue(QUEUE_3, true));

        Map<String, Object> arguments4 = new HashMap<>();
        arguments4.put("x-dead-letter-exchange", DEAD_LETTER_EXCHANGE);
        arguments4.put("x-dead-letter-routing-key", DEAD_LETTER_ROUTING_KEY);
        declarable.add(new Queue(QUEUE_4, true, false, false, arguments4));

        Map<String, Object> arguments5 = new HashMap<>();
        arguments5.put("x-max-length", 3);
        declarable.add(new Queue(QUEUE_5, true, false, false, arguments5));

        declarable.add(new Queue(DEAD_LETTER_QUEUE, true));
        //********************************************* Add Exchange ***************************************//
        declarable.add(new DirectExchange(DIRECT_EXCHANGE, true, false));
        declarable.add(new DirectExchange(DEAD_LETTER_EXCHANGE, true, false));
        //********************************************* Add Binding ***************************************//
        declarable.addAll(directBinding());
        return new Declarables(declarable);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerPrefetchFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPrefetchCount(2);
        return factory;
    }
}

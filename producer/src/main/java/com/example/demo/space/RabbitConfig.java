package com.example.demo.space;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;

import static com.example.demo.space.RabbitNames.*;

@Configuration
public class RabbitConfig {
    private final String HOST = "localhost";
    private final Integer PORT = 5672;
    private final String USERNAME = "guest";
    private final String PASSWORD = "guest";

    @Bean
    RabbitAdmin rabbitAdmin(RabbitTemplate rabbitTemplate) {
        return new RabbitAdmin(rabbitTemplate);
    }

    @Bean
    RabbitTemplate rabbitTemplate(final @Qualifier("report-rabbit") ConnectionFactory connectionFactory) {
        RabbitTemplate rt = new RabbitTemplate(connectionFactory);
        rt.setMessageConverter(producerJackson2MessageConverter());
        ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy();
        policy.setInitialInterval(2000);
        policy.setMaxInterval(60000);
        policy.setMultiplier(3);
        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setBackOffPolicy(policy);
        rt.setRetryTemplate(retryTemplate);
        return rt;
    }

    @Bean("report-rabbit")
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(HOST);
        connectionFactory.setPort(PORT);
        connectionFactory.setUsername(USERNAME);
        connectionFactory.setPassword(PASSWORD);
        connectionFactory.setRequestedHeartBeat(15);
        connectionFactory.setConnectionTimeout(500);
        return connectionFactory;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue mainQueue() {
        return QueueBuilder.durable(JURIDIC_ENTITY)
                .ttl(10_000)
                .withArgument("x-dead-letter-exchange", DELAY_EXCHANGE_DLX)
                .withArgument("x-dead-letter-routing-key", JURIDIC_PAYMENT_KEY_DLX)
                .build();
    }

    @Bean
    public DirectExchange mainExchange() {
        return new DirectExchange(DELAY_EXCHANGE);
    }

    @Bean
    public Binding mainBinding() {
        return BindingBuilder.bind(mainQueue()).to(mainExchange()).with(JURIDIC_PAYMENT_KEY);
    }

    @Bean
    public Queue dlq() {
        return QueueBuilder.durable(JURIDIC_ENTITY_DLQ).build();
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(DELAY_EXCHANGE_DLX);
    }

    @Bean
    public Binding dlqBinding() {
        return BindingBuilder.bind(dlq()).to(dlxExchange()).with(JURIDIC_PAYMENT_KEY_DLX);
    }
}

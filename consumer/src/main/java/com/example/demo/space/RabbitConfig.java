package com.example.demo.space;

import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    private final String HOST = "localhost";
    private final Integer PORT = 5672;
    private final String USERNAME = "guest";
    private final String PASSWORD = "guest";
    private final Integer CONCURRENCY = 3;
    private final Integer MAX_CONCURRENCY = 3;
    private final Integer RETRY_MAX_ATTEMPTS = 5;
    private final Long RETRY_INITIAL_INTERVAL = 5000L;
    private final Double RETRY_MULTIPLIER = 2.0;
    private final Long RETRY_MAX_INTERVAL = 30_000L;

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(CONCURRENCY);
        factory.setMaxConcurrentConsumers(MAX_CONCURRENCY);
        factory.setAdviceChain(RetryInterceptorBuilder.stateless()
                .maxAttempts(RETRY_MAX_ATTEMPTS)
                .backOffOptions(RETRY_INITIAL_INTERVAL, RETRY_MULTIPLIER, RETRY_MAX_INTERVAL) // initialInterval, multiplier, maxInterval
                .build());
        factory.setMessageConverter(messageConverter());
        return factory;
    }

    @Bean("juridic-rabbit")
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

    @Bean(name = "jacksonMessageConverter")
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}

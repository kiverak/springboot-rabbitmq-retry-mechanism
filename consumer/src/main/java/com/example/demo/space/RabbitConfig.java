package com.example.demo.space;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

@Configuration
public class RabbitConfig {
    private final String HOST = "localhost";
    private final Integer PORT = 5672;
    private final String USERNAME = "guest";
    private final String PASSWORD = "guest";
    private final Integer CONCURRENCY = 5;
    private final Integer MAX_CONCURRENCY = 5;
    private final Boolean DEFAULT_REQUEUE_REJECTED = true;
    private final Boolean RETRY_ENABLED = true;
    private final Integer RETRY_MAX_ATTEMPTS = 5;
    private final Integer RETRY_INITIAL_INTERVAL = 5_000;
    private final Integer RETRY_MULTIPLIER = 2;
    private final Integer RETRY_MAX_INTERVAL = 60_000;

    @Bean
    public SimpleRabbitListenerContainerFactory listenerContainerFactory(ConnectionFactory connectionFactory) {
        final var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        factory.setConcurrentConsumers(CONCURRENCY);
        factory.setMaxConcurrentConsumers(MAX_CONCURRENCY);
        factory.setDefaultRequeueRejected(DEFAULT_REQUEUE_REJECTED);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);

        if (RETRY_ENABLED) {
            RetryOperationsInterceptor retryInterceptor = RetryInterceptorBuilder.stateless()
                    .maxAttempts(RETRY_MAX_ATTEMPTS)
                    .backOffOptions(RETRY_INITIAL_INTERVAL, RETRY_MULTIPLIER, RETRY_MAX_INTERVAL)
                    .recoverer(new RejectAndDontRequeueRecoverer())
                    .build();
            factory.setAdviceChain(retryInterceptor);
        }

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

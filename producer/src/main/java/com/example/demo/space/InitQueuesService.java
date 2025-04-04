package com.example.demo.space;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

import static com.example.demo.space.RabbitNames.*;

@Service
@RequiredArgsConstructor
public class InitQueuesService {

    private final RabbitAdmin rabbitAdmin;

    @PostConstruct
    public void create() {
        String dqlName = JURIDIC_ENTITY + "-dlq";
        String dqlExchangeName = JURIDIC_ENTITY + "-dlq-exchange";

//        rabbitAdmin.deleteQueue(dqlName);
//        rabbitAdmin.deleteQueue(JURIDIC_ENTITY);
//        rabbitAdmin.deleteExchange(dqlExchangeName);
//        rabbitAdmin.deleteExchange(DELAY_EXCHANGE);


        Queue queue = QueueBuilder.durable(JURIDIC_ENTITY)
                .deadLetterExchange(dqlExchangeName)
                .build();
        Exchange exchange = ExchangeBuilder.directExchange(DELAY_EXCHANGE).build();
        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareBinding(new Binding(JURIDIC_ENTITY, Binding.DestinationType.QUEUE,
                DELAY_EXCHANGE, JURIDIC_PAYMENT_KEY, null));

        rabbitAdmin.declareQueue(QueueBuilder.nonDurable(dqlName).build());
        rabbitAdmin.declareExchange(ExchangeBuilder.directExchange(dqlExchangeName).build());
        rabbitAdmin.declareBinding(new Binding(dqlName, Binding.DestinationType.QUEUE,
                dqlExchangeName, JURIDIC_PAYMENT_KEY, null));
    }
}

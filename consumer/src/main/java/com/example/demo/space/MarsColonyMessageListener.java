package com.example.demo.space;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import static com.example.demo.space.RabbitNames.JURIDIC_ENTITY;
import static com.example.demo.space.RabbitNames.JURIDIC_ENTITY_DLQ;

@Service
@Slf4j
public class MarsColonyMessageListener {

    @RabbitListener(queues = JURIDIC_ENTITY)
    public void onMessage(String message) {
        log.info("JURIDIC_ENTITY " + message);
//        throw new RuntimeException("on purpose...");
    }

    @RabbitListener(queues = JURIDIC_ENTITY_DLQ)
    public void onMessageDlq(String message) {
        log.info("JURIDIC_ENTITY_DLQ " + message);
//        throw new RuntimeException("on purpose...");
    }

}

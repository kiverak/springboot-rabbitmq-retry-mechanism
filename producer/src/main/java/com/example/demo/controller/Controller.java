package com.example.demo.controller;

import com.example.demo.space.RabbitNames;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class Controller {

    private final RabbitTemplate rabbitTemplate;

    @PostMapping("/send")
    public void send(@RequestBody String message) {
        rabbitTemplate.convertAndSend(RabbitNames.DELAY_EXCHANGE, RabbitNames.JURIDIC_PAYMENT_KEY, message);
    }
}

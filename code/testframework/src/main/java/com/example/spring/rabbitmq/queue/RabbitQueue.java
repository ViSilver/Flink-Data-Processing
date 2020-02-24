package com.example.spring.rabbitmq.queue;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@RequiredArgsConstructor
public class RabbitQueue {

    @Value("${queue-name}")
    private String queueName;

}

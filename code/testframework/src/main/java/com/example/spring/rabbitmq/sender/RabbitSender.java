package com.example.spring.rabbitmq.sender;

import com.example.spring.rabbitmq.model.Apartment;
import com.example.spring.rabbitmq.queue.RabbitQueue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class RabbitSender {

    private final RabbitQueue rabbitQueue;

    private final RabbitTemplate rabbitTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void send(final String stringMessage) {
        log.info("Sending message to RabbitMQ on queue: {}", rabbitQueue.getQueueName());
        rabbitTemplate.convertAndSend(rabbitQueue.getQueueName(), stringMessage);
    }

    public void send(final Apartment apartment) {
        rabbitTemplate.convertAndSend(rabbitQueue.getQueueName(), apartment);
    }
}

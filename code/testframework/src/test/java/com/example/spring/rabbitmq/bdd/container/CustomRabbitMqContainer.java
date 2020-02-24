package com.example.spring.rabbitmq.bdd.container;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.wait.strategy.Wait;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CustomRabbitMqContainer {

//    public static final RabbitMQContainer CONTAINER;

    public static final int RABBIT_MQ_PORT = 5672;

    static {
//        CONTAINER = new RabbitMQContainer("rabbitmq:management")
//                .withExposedPorts(RABBIT_MQ_PORT)
//                .withQueue("hello")
//                .waitingFor(Wait.forLogMessage("^.*Server startup complete;.*$", 1));
    }
}

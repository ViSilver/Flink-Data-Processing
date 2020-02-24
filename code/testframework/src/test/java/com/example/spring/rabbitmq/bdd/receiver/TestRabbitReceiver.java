package com.example.spring.rabbitmq.bdd.receiver;

import com.example.spring.rabbitmq.bdd.stepdef.ScenarioContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TestRabbitReceiver {

    private final ScenarioContext scenarioContext;

//    @RabbitListener(containerFactory = "myRabbitListenerContainerFactory", queues = "")
    public void rabbitReceive(@Payload final String stringMessage) {
        log.info("Received a rabbit message: {}", stringMessage);
        scenarioContext.getReceivedRabbitMessages().add(stringMessage);
    }

    @RabbitListener(containerFactory = "myRabbitListenerContainerFactory", queues = "my.output")
    public void alarmStatisticsRecordReceiver(@Payload final String stringMessage) {
        log.info("Received a record statistics event: {}", stringMessage);
        scenarioContext.getReceivedRabbitMessages().add(stringMessage);
    }

}

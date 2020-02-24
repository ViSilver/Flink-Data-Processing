package com.example.spring.rabbitmq.bdd.stepdef;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@Getter
public class ScenarioContext {

    private final List<String> receivedRabbitMessages = Collections.synchronizedList(new ArrayList<>());
}

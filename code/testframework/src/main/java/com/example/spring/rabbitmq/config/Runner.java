package com.example.spring.rabbitmq.config;

import com.example.spring.rabbitmq.sender.RabbitSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

//@Component
@Slf4j
@RequiredArgsConstructor
public class Runner implements CommandLineRunner {

    private final RabbitSender rabbitSender;

    @Override
    public void run(String... args) throws Exception {
//        rabbitSender.send("Hello World!");
    }
}

package com.example.spring.rabbitmq.bdd.config;

import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.domain.QueueInfo;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Profile("test")
@Configuration
@EnableAutoConfiguration
public class TestConfig {

//    private static final RabbitMQContainer rabbitMQContainer = CustomRabbitMqContainer.CONTAINER;
//    private static final GenericContainer cassandraContainer = CassandraContainer.CONTAINER;

    static {
//        rabbitMQContainer.start();
//        cassandraContainer.start();
    }

    private static String DEAD_LETTER_QUEUE_NAME = "dead_letter_queue";

    @Value("${broker.host}")
    private String brokerHost;

    @Value("${broker.port}")
    private int brokerPort;

    @Value("${broker.admin.port}")
    private int brokerAdminPort;

    @Value("${broker.username}")
    private String brokerUsername;

    @Value("${broker.password}")
    private String brokerPassword;

    @Bean
    public ConnectionFactory rabbitMqConnectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
//        connectionFactory.setAddresses("localhost:" + rabbitMQContainer.getMappedPort(CustomRabbitMqContainer.RABBIT_MQ_PORT));
        connectionFactory.setHost(brokerHost);
        connectionFactory.setPort(brokerPort);
        connectionFactory.setUsername(brokerUsername);
        connectionFactory.setPassword(brokerPassword);
        connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
        connectionFactory.setExecutor(rabbitExecutorPool());
        return connectionFactory;
    }

    @Bean
    public Executor rabbitExecutorPool() {
        return Executors.newCachedThreadPool();
    }

    @Bean
    public Client rabbitMqAdminClient() throws MalformedURLException, URISyntaxException {
        final Client adminClient = new Client("http://" + brokerHost + ":" + brokerAdminPort + "/api/", brokerUsername,
                                   brokerPassword);

        adminClient.declareQueue("/", "my.output", new QueueInfo(true, false, false));
        adminClient.declareQueue("/", "late.events", new QueueInfo(true, false, false));
        return adminClient;
    }

//    @Bean
//    public RabbitAdmin rabbitAdmin(final ConnectionFactory connectionFactory) {
//        return new RabbitAdmin(connectionFactory);
//    }
//
//    @Bean
//    public Queue totalBatteryVoltageQueue() {
//        return QueueBuilder.durable("total.battery.voltage.queue").deadLetterExchange(DEAD_LETTER_QUEUE_NAME).build();
//    }
//
//    @Bean
//    public Exchange alarmsExchange() {
//        return ExchangeBuilder.directExchange("alarms").durable(true).build();
//    }
//
//    @Bean
//    public Binding totalBatteryVoltageBinding() {
//        return BindingBuilder.bind(totalBatteryVoltageQueue()).to(alarmsExchange()).with("141").noargs();
//    }
//
//    @Bean
//    public Queue deadLetterQueue() {
//        return QueueBuilder.durable(DEAD_LETTER_QUEUE_NAME).build();
//    }
}

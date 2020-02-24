package com.example.spring.rabbitmq.bdd.stepdef;

import com.example.spring.rabbitmq.Application;
import com.example.spring.rabbitmq.bdd.config.ParserConfig;
import com.example.spring.rabbitmq.bdd.config.TestConfig;
import com.example.spring.rabbitmq.bdd.model.AlarmRecord;
import com.example.spring.rabbitmq.model.Apartment;
import com.example.spring.rabbitmq.sender.RabbitSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.http.client.Client;
import com.rabbitmq.http.client.domain.ExchangeInfo;
import com.rabbitmq.http.client.domain.QueueInfo;
import com.univocity.parsers.csv.CsvRoutines;
import cucumber.api.java.Before;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.cucumber.datatable.DataTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.awaitility.Awaitility.await;

@Slf4j
@RequiredArgsConstructor
@ActiveProfiles("test")
@SpringBootTest(classes = {Application.class, TestConfig.class, ParserConfig.class})
//@ContextConfiguration(classes = {Application.class, TestConfig.class}, loader = SpringBootContextLoader.class)
public class BaseSteps {

    private static final String RABBIT_MESSAGE = "Hello World from BDD test!";
    private static final String TOTAL_BATTERY_VOLTAGE_QUEUE_NAME = "total.battery.voltage.queue";
    private static final String ALARMS_EXCHANGE_NAME = "alarms";

    private final RabbitSender rabbitSender;

    private final RabbitTemplate rabbitTemplate;

    private final ScenarioContext scenarioContext;

    private final CsvRoutines csvRoutines;

    private final Client rabbitMqAdminClient;

    private final Jackson2JsonMessageConverter messageConverter;

    @Value("${broker.host}")
    private String brokerHost;

    @Value("${broker.admin.port}")
    private int brokerAdminPort;

    @Value("${broker.username}")
    private String brokerUsername;

    @Value("${broker.password}")
    private String brokerPassword;

    @Before
    public void beforeScenario() throws MalformedURLException, URISyntaxException {
        scenarioContext.getReceivedRabbitMessages().clear();
    }

    @When("User sends (.*) message")
    public void userSendsAMessage(long messages) {
        log.info("Sending message");
        LongStream.rangeClosed(0, messages).forEach(id -> {
            Apartment apartment = new Apartment(id, "name", 1L, "hostName",
                                                "neighbourhoodGroup", "neighbourhood", "lat",
                                                "long", "roomType", 12L, 3L, 1L, "lastReview", "revPerMonth", "calc",
                                                12L);
            rabbitSender.send(apartment);
        });
    }

    @Then("A message is received")
    public void aMessageIsReceived() {
        await().alias("Desired rabbit message was not received")
               .atMost(3L, TimeUnit.SECONDS)
               .pollInterval(1L, TimeUnit.SECONDS)
               .until(() -> scenarioContext.getReceivedRabbitMessages().stream().anyMatch(RABBIT_MESSAGE::equals));
    }

    @When("Events are read from file (.*) and sent to exchange (.*)")
    public void eventsAreReadFromFileAndSentToQueue(final String fileName,
                                                    final String exchangeName) throws FileNotFoundException {
        final File csvFile = ResourceUtils.getFile("classpath:data/" + fileName);

        Iterable<AlarmRecord> iterable = csvRoutines.iterate(AlarmRecord.class, csvFile);
        Stream<AlarmRecord> stream = StreamSupport.stream(iterable.spliterator(), true);
        log.info("Beginning reading the file");
        long begin = System.currentTimeMillis();

        stream.forEach(record -> {
            if (303 == record.getAlarmId()) {
                rabbitTemplate.convertAndSend(exchangeName, "303", record);
            } else {
                rabbitTemplate.convertAndSend(exchangeName, String.valueOf(record.getDataPointId()), record);
            }
        });

        long end = System.currentTimeMillis();
        log.info("Insertion took {} seconds", (end - begin) / 1_000L);
    }

    @Then("User receives the report on queue (.*)")
    public void userReceivesTheReportOnQueue(final String queueName) {

    }

    @Given("The exchanges are created")
    public void theExchangeAndQueuesAreCreated(final DataTable dataTable) {
        dataTable.asMaps().forEach(row -> {
            final ExchangeInfo exchangeInfo = new ExchangeInfo();

            exchangeInfo.setDurable(Boolean.getBoolean(row.get("Durable")));
            exchangeInfo.setType(row.get("Type"));
            exchangeInfo.setName(row.get("Name"));

            rabbitMqAdminClient.declareExchange("/", row.get("Name"), exchangeInfo);
            log.info("Exchange {} is created", row.get("Name"));
        });
    }

    @Given("The queues are created")
    public void theQueuesAreCreated(final DataTable dataTable) {
        dataTable.asMaps().forEach(row -> {
            QueueInfo queueInfo = new QueueInfo();
            queueInfo.setDurable(Boolean.parseBoolean(row.get("Durable")));
            queueInfo.setName(row.get("Name"));

            log.info("Queue {} is created {}", row.get("Name"), Boolean.parseBoolean(row.get("Durable")));
            rabbitMqAdminClient.deleteQueue("/", row.get("Name"));
            rabbitMqAdminClient.declareQueue("/", row.get("Name"), queueInfo);
        });
    }

    @Given("The bindings are set")
    public void theBindingsAreSet(final DataTable dataTable) {
        dataTable.asMaps().forEach(row -> {
            rabbitMqAdminClient.bindQueue("/", row.get("Queue"), row.get("Exchange"), row.get("Routing key"));
            log.info("Queue {} binded to exchange {} using routing key '{}'", row.get("Queue"), row.get("Exchange"),
                     row.get("Routing key"));
        });
    }

    @Then("User waits for {int} minutes for logs")
    public void userWaitsForMinutesForLogs(int minutes) {
        await().timeout(Duration.ofMinutes(minutes + 1)).pollDelay(Duration.ofMinutes(minutes)).until(() -> true);
    }

    @When("Events are read from file (.*) and sent with error rate 1 out of (.*) to exchange (.*)")
    public void eventsAreReadFromFileAndSentWithErrorsToExchange(final String fileName,
                                                                 final int bound,
                                                                 final String exchangeName) throws FileNotFoundException {
        final File csvFile = ResourceUtils.getFile("classpath:data/" + fileName);

        final Random random = new Random();

        Iterable<AlarmRecord> iterable = csvRoutines.iterate(AlarmRecord.class, csvFile);
        Stream<AlarmRecord> stream = StreamSupport.stream(iterable.spliterator(), true);
        log.info("Beginning reading the file");
        long begin = System.currentTimeMillis();

        stream.forEach(record -> {
            if (1 == random.nextInt(bound)) {
                record.setEventTime(null);
            }
            if (303 == record.getAlarmId()) {
                rabbitTemplate.convertAndSend(exchangeName, "303", record);
            } else {
                rabbitTemplate.convertAndSend(exchangeName, String.valueOf(record.getDataPointId()), record);
            }
        });

        long end = System.currentTimeMillis();
        log.info("Insertion took {} seconds", (end - begin) / 1_000L);
    }
}

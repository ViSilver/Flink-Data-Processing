package com.example.spring.rabbitmq.bdd.container;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CassandraContainer {

//    public static final GenericContainer CONTAINER;
    public static final String IMAGE_VERSION = "2.1.21";
    public static final int CASSANDRA_PORT = 7001;

    static {
//        CONTAINER = new GenericContainer("cassandra:" + IMAGE_VERSION)
//                .withExposedPorts(CASSANDRA_PORT)
//                .withCommand("-Dcassandra.config=/path/to/cassandra.yaml")
//                .waitingFor(Wait.forLogMessage("^.*Listening for thrift clients.*$", 1));
    }
}

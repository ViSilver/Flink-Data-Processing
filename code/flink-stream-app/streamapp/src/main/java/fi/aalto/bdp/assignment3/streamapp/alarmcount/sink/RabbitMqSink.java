package fi.aalto.bdp.assignment3.streamapp.alarmcount.sink;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSink;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSinkPublishOptions;
import org.apache.flink.streaming.connectors.rabbitmq.SerializableReturnListener;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;

import java.io.IOException;

public class RabbitMqSink<IN> extends RMQSink<IN> {

    public RabbitMqSink(RMQConnectionConfig rmqConnectionConfig,
                        String queueName, SerializationSchema<IN> schema) {
        super(rmqConnectionConfig, queueName, schema);
    }

    public RabbitMqSink(RMQConnectionConfig rmqConnectionConfig,
                        SerializationSchema<IN> schema,
                        RMQSinkPublishOptions<IN> publishOptions) {
        super(rmqConnectionConfig, schema, publishOptions);
    }

    public RabbitMqSink(RMQConnectionConfig rmqConnectionConfig,
                        SerializationSchema<IN> schema,
                        RMQSinkPublishOptions<IN> publishOptions,
                        SerializableReturnListener returnListener) {
        super(rmqConnectionConfig, schema, publishOptions, returnListener);
    }

    @Override
    protected void setupQueue() throws IOException {
        if (queueName != null) {
            channel.queueDeclare(queueName, true, false, false, null);
        }
    }
}

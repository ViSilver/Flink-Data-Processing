package fi.aalto.bdp.assignment3.streamapp.alarmcount;

import com.fasterxml.jackson.databind.ObjectMapper;
import fi.aalto.bdp.assignment3.streamapp.alarmcount.functions.CountingAggregator;
import fi.aalto.bdp.assignment3.streamapp.alarmcount.functions.LowPowerGridVoltageAlarmCollector;
import fi.aalto.bdp.assignment3.streamapp.alarmcount.marshalling.AlarmRecordUnmarshaller;
import fi.aalto.bdp.assignment3.streamapp.alarmcount.model.AlarmRecord;
import fi.aalto.bdp.assignment3.streamapp.alarmcount.model.LowPowerGridVoltageAlarm;
import fi.aalto.bdp.assignment3.streamapp.alarmcount.sink.RabbitMqSink;
import fi.aalto.bdp.assignment3.streamapp.alarmcount.watermark.LastNEventsWatermark;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSink;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import org.apache.flink.util.OutputTag;

import java.util.TimeZone;

public class PowerGridVoltageCount {
    private static final String CHECKPOINTING_OPTION = "checkpointing";
    private static final String EVENT_TIME_OPTION = "event-time";
    private static final String POWER_GRID_VOLTAGE_QUEUE_NAME = "power.grid.voltage.queue";

    private static final Time WINDOW_SIZE = Time.minutes(10L);

    private static final String BROKER_HOST = "rabbitmq";
    private static final int BROKER_PORT = 5672;
    private static final RMQConnectionConfig RABBIT_MQ_CONNECTION_CONFIG;

    private static final AlarmRecordUnmarshaller ALARM_RECORD_UNMARSHALLER;

    static {
        RABBIT_MQ_CONNECTION_CONFIG = new RMQConnectionConfig.Builder()
                .setHost(BROKER_HOST)
                .setPort(BROKER_PORT)
                .setUserName("guest")
                .setPassword("guest")
                .setVirtualHost("/")
                .setTopologyRecoveryEnabled(true)
                .build();

        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setTimeZone(TimeZone.getTimeZone("UTC"));

        ALARM_RECORD_UNMARSHALLER = new AlarmRecordUnmarshaller(objectMapper);
    }

    public static void main(String[] args) throws Exception {
        final ParameterTool params = ParameterTool.fromArgs(args);
        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        configureEnvironment(params, env);

        SingleOutputStreamOperator<AlarmRecord> alarms = env.addSource(rabbitSource())
                                                            .name("AlarmRecord Source")
                                                            .flatMap(ALARM_RECORD_UNMARSHALLER)
                                                            .setParallelism(5)
                                                            .assignTimestampsAndWatermarks(new LastNEventsWatermark(5))
                                                            .setParallelism(5);

        final OutputTag<AlarmRecord> lateEventsTag = new OutputTag<AlarmRecord>("late-events") {};

        SingleOutputStreamOperator<LowPowerGridVoltageAlarm> statistics = alarms
                .keyBy(new KeySelector<AlarmRecord, Tuple3<Integer, Long, Integer>>() {
                    @Override
                    public Tuple3<Integer, Long, Integer> getKey(AlarmRecord value) throws Exception {
                        return new Tuple3<>(value.getDataPointId(), value.getStationId(), value.getAlarmId());
                    }
                })
                .timeWindow(WINDOW_SIZE)
                .allowedLateness(Time.minutes(15L))
                .sideOutputLateData(lateEventsTag)
                .aggregate(new CountingAggregator(), new LowPowerGridVoltageAlarmCollector())
                .setParallelism(5)
                .name("LowPowerGridVoltageAlarm count");

        DataStream<String> stringStatistics = statistics.map(ALARM_RECORD_UNMARSHALLER::toStringMessage)
                                                        .setMaxParallelism(10);
        stringStatistics.addSink(rabbitMqSink("my.output"))
                        .name("LowPowerGridVoltageAlarm sink");

        statistics.getSideOutput(lateEventsTag)
                  .map(ALARM_RECORD_UNMARSHALLER::toStringMessage)
                  .setParallelism(1)
                  .addSink(rabbitMqSink("late.events"))
                  .setParallelism(1)
                  .name("Late events sink");

        env.execute("BTS Alarm Count");
    }

    private static RMQSink<String> rabbitMqSink(final String queueName) {
        return new RabbitMqSink<>(RABBIT_MQ_CONNECTION_CONFIG, queueName, new SimpleStringSchema());
    }

    private static RMQSource<String> rabbitSource() {
        return new RMQSource<>(RABBIT_MQ_CONNECTION_CONFIG,
                               POWER_GRID_VOLTAGE_QUEUE_NAME,
                               false,
                               new SimpleStringSchema());
    }

    private static void configureEnvironment(final ParameterTool params, final StreamExecutionEnvironment env) {
        boolean checkpointingEnabled = params.has(CHECKPOINTING_OPTION);
        boolean eventTimeSemantics = params.has(EVENT_TIME_OPTION);

        if (checkpointingEnabled) {
            env.enableCheckpointing(1000);
        }

        if (eventTimeSemantics) {
            env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        }

        //disabling Operator chaining to make it easier to follow the Job in the WebUI
        env.disableOperatorChaining();
    }
}

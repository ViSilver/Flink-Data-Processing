package com.example.spring.rabbitmq.bdd.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.univocity.parsers.annotations.Format;
import com.univocity.parsers.annotations.Headers;
import com.univocity.parsers.annotations.Parsed;
import com.univocity.parsers.common.record.Record;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Headers(sequence = {"station_id", "datapoint_id", "alarm_id", "event_time", "value", "valueThreshold", "isActive"},
        extract = true)
public class AlarmRecord implements Serializable {

    @Parsed(field = "station_id", defaultNullRead = "0")
    private long stationId;

    @Parsed(field = "datapoint_id", defaultNullRead = "0")
    private int dataPointId;

    @Parsed(field = "alarm_id", defaultNullRead = "0")
    private int alarmId;

    @Parsed(field = "event_time")
    @Format(formats = {"yyyy-MM-dd HH:mm:ss z"})
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss z")
    private Date eventTime;

    @Parsed(field = "value", defaultNullRead = "0")
    private double value;

    @Parsed(field = "valueThreshold", defaultNullRead = "0")
    private double valueThreshold;

    @Parsed(field = "isActive", defaultNullRead = "false")
    private boolean isActive;

//    @Parsed(field = "storedtime", defaultNullRead = "")
//    private final String storedTime;

}

package fi.aalto.bdp.assignment3.streamapp.alarmcount.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlarmRecord {

    private long stationId;

    private int dataPointId;

    private int alarmId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss z")
    private Date eventTime;

    private double value;

    private double valueThreshold;

    private boolean isActive;
}

package fi.aalto.bdp.assignment3.streamapp.alarmcount.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EquipmentFailedAlarm {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss z")
    private Date windowStart;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss z")
    private Date windowEnd;

    private int datapointId;

    private long stationId;

    private int alarmId;

    private long nrOfTimesEquipmentFailed;
}

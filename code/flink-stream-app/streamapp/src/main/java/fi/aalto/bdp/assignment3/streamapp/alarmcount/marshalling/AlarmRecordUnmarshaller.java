package fi.aalto.bdp.assignment3.streamapp.alarmcount.marshalling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.aalto.bdp.assignment3.streamapp.alarmcount.model.AlarmRecord;
import fi.aalto.bdp.assignment3.streamapp.alarmcount.model.BatteryVoltageAlarm;
import fi.aalto.bdp.assignment3.streamapp.alarmcount.model.EquipmentFailedAlarm;
import fi.aalto.bdp.assignment3.streamapp.alarmcount.model.LowPowerGridVoltageAlarm;
import lombok.RequiredArgsConstructor;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;

@RequiredArgsConstructor
public class AlarmRecordUnmarshaller implements FlatMapFunction<String, AlarmRecord> {

    private final ObjectMapper objectMapper;

    @Override
    public void flatMap(String value, Collector<AlarmRecord> out) throws Exception {
        out.collect(objectMapper.readValue(value, AlarmRecord.class));
    }

    public String toStringMessage(final AlarmRecord alarmRecord) throws JsonProcessingException {
        return objectMapper.writeValueAsString(alarmRecord);
    }

    public String toStringMessage(final BatteryVoltageAlarm alarmRecord) throws JsonProcessingException {
        return objectMapper.writeValueAsString(alarmRecord);
    }

    public String toStringMessage(final EquipmentFailedAlarm alarmRecord) throws JsonProcessingException {
        return objectMapper.writeValueAsString(alarmRecord);
    }

    public String toStringMessage(final LowPowerGridVoltageAlarm alarm) throws JsonProcessingException {
        return objectMapper.writeValueAsString(alarm);
    }
}

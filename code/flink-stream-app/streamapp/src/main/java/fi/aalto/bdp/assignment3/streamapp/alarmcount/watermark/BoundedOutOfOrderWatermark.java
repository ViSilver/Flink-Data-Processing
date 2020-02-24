package fi.aalto.bdp.assignment3.streamapp.alarmcount.watermark;

import fi.aalto.bdp.assignment3.streamapp.alarmcount.model.AlarmRecord;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;

public class BoundedOutOfOrderWatermark extends BoundedOutOfOrdernessTimestampExtractor<AlarmRecord> {

    public BoundedOutOfOrderWatermark(Time maxOutOfOrderness) {
        super(maxOutOfOrderness);
    }

    @Override
    public long extractTimestamp(AlarmRecord element) {
        return element.getEventTime().getTime();
    }
}

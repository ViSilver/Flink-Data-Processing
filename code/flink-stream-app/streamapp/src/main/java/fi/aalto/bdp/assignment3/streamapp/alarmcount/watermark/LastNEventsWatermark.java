package fi.aalto.bdp.assignment3.streamapp.alarmcount.watermark;

import fi.aalto.bdp.assignment3.streamapp.alarmcount.model.AlarmRecord;
import lombok.RequiredArgsConstructor;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.watermark.Watermark;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.Optional;
import java.util.TreeSet;

@RequiredArgsConstructor
public class LastNEventsWatermark implements AssignerWithPeriodicWatermarks<AlarmRecord> {

    private final TreeSet<Long> lastXTimestamps = new TreeSet<>();

    private final int numberOfLastEvents;

    @Nullable
    @Override
    public Watermark getCurrentWatermark() {
        if (!lastXTimestamps.isEmpty() && lastXTimestamps.size() < numberOfLastEvents) {
            return new Watermark(lastXTimestamps.first());
        }

        return Optional.ofNullable(lastXTimestamps.pollFirst()).map(Watermark::new).orElse(null);
    }

    @Override
    public long extractTimestamp(AlarmRecord element, long previousElementTimestamp) {
        long timestamp = Optional.ofNullable(element.getEventTime())
                                 .map(Date::getTime)
                                 .orElse(previousElementTimestamp);
        lastXTimestamps.add(timestamp);
        return timestamp;
    }
}

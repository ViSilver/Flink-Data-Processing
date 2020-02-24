package fi.aalto.bdp.assignment3.streamapp.alarmcount.functions;

import fi.aalto.bdp.assignment3.streamapp.alarmcount.model.AlarmRecord;
import org.apache.flink.api.common.functions.AggregateFunction;

public class CountingAggregator implements AggregateFunction<AlarmRecord, Long, Long> {

    @Override
    public Long createAccumulator() {
        return 0L;
    }

    @Override
    public Long add(AlarmRecord alarmRecord, Long accumulator) {
        return accumulator + 1L;
    }

    @Override
    public Long getResult(Long accumulator) {
        return accumulator;
    }

    @Override
    public Long merge(Long a, Long b) {
        return a + b;
    }
}

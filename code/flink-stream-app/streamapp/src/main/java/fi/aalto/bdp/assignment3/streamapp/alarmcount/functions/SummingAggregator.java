package fi.aalto.bdp.assignment3.streamapp.alarmcount.functions;

import fi.aalto.bdp.assignment3.streamapp.alarmcount.model.AlarmRecord;
import org.apache.flink.api.common.functions.AggregateFunction;

public class SummingAggregator implements AggregateFunction<AlarmRecord, Double, Double> {

    @Override
    public Double createAccumulator() {
        return 0.0;
    }

    @Override
    public Double add(AlarmRecord alarmRecord, Double accumulator) {
        return accumulator + alarmRecord.getValue();
    }

    @Override
    public Double getResult(Double accumulator) {
        return accumulator;
    }

    @Override
    public Double merge(Double a, Double b) {
        return a + b;
    }
}

package fi.aalto.bdp.assignment3.streamapp.alarmcount.functions;

import fi.aalto.bdp.assignment3.streamapp.alarmcount.model.LowPowerGridVoltageAlarm;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.Date;

public class LowPowerGridVoltageAlarmCollector
        extends ProcessWindowFunction<Long, LowPowerGridVoltageAlarm, Tuple3<Integer, Long, Integer>, TimeWindow> {

    @Override
    public void process(Tuple3<Integer, Long, Integer> key,
                        Context context,
                        Iterable<Long> iterable,
                        Collector<LowPowerGridVoltageAlarm> collector) throws Exception {
        Long count = iterable.iterator().next();

        Date startTime = new Date(context.window().getStart());
        Date endTime = new Date(context.window().getEnd());

        collector.collect(new LowPowerGridVoltageAlarm(startTime, endTime, key.f0, key.f1, key.f2, count));
    }
}

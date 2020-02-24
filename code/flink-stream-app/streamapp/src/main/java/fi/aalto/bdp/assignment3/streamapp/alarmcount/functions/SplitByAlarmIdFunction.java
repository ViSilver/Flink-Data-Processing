package fi.aalto.bdp.assignment3.streamapp.alarmcount.functions;

import fi.aalto.bdp.assignment3.streamapp.alarmcount.model.EquipmentFailedAlarm;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

public class SplitByAlarmIdFunction
        extends ProcessWindowFunction<EquipmentFailedAlarm, EquipmentFailedAlarm, Tuple3<Integer, Long, Integer>, TimeWindow> {


    private final OutputTag<EquipmentFailedAlarm> outputTag;

    public SplitByAlarmIdFunction(String outputTag) {
        this.outputTag = new OutputTag<>(outputTag);
    }

    @Override
    public void process(Tuple3<Integer, Long, Integer> key,
                        Context context,
                        Iterable<EquipmentFailedAlarm> elements,
                        Collector<EquipmentFailedAlarm> out) throws Exception {
        EquipmentFailedAlarm analyticsResult = elements.iterator().next();

        out.collect(analyticsResult);

        // emit data to side output
        context.output(outputTag, analyticsResult);

    }
}

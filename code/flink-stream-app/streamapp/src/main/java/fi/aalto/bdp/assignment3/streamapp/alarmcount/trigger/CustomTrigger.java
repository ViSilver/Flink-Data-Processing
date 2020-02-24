package fi.aalto.bdp.assignment3.streamapp.alarmcount.trigger;

import fi.aalto.bdp.assignment3.streamapp.alarmcount.model.AlarmRecord;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.streaming.api.functions.windowing.delta.DeltaFunction;
import org.apache.flink.streaming.api.windowing.triggers.Trigger;
import org.apache.flink.streaming.api.windowing.triggers.TriggerResult;
import org.apache.flink.streaming.api.windowing.windows.Window;

public class CustomTrigger<W extends Window> extends Trigger<AlarmRecord, W> {

    private static final long serialVersionUID = 1L;

    private final DeltaFunction<AlarmRecord> deltaFunction;
    private final double threshold;
    private final ValueStateDescriptor<AlarmRecord> stateDesc;

    private CustomTrigger(double threshold, DeltaFunction<AlarmRecord> deltaFunction, TypeSerializer<AlarmRecord> stateSerializer) {
        this.deltaFunction = deltaFunction;
        this.threshold = threshold;
        stateDesc = new ValueStateDescriptor<>("last-element", stateSerializer);

    }

    @Override
    public TriggerResult onElement(AlarmRecord element, long timestamp, W window, TriggerContext ctx) throws Exception {
        ValueState<AlarmRecord> lastElementState = ctx.getPartitionedState(stateDesc);
        if (lastElementState.value() == null) {
            lastElementState.update(element);
            return TriggerResult.CONTINUE;
        }
        if (deltaFunction.getDelta(lastElementState.value(), element) > this.threshold) {
            lastElementState.update(element);
            return TriggerResult.FIRE;
        }
        return TriggerResult.CONTINUE;
    }

    @Override
    public TriggerResult onEventTime(long time, W window, TriggerContext ctx) {
        return TriggerResult.CONTINUE;
    }

    @Override
    public TriggerResult onProcessingTime(long time, W window, TriggerContext ctx) throws Exception {
        return TriggerResult.FIRE;
    }

    @Override
    public void clear(W window, TriggerContext ctx) throws Exception {
        ctx.getPartitionedState(stateDesc).clear();
    }

    @Override
    public String toString() {
        return "DeltaTrigger(" +  deltaFunction + ", " + threshold + ")";
    }

    /**
     * Creates a delta trigger from the given threshold and {@code DeltaFunction}.
     *
     * @param threshold The threshold at which to trigger.
     * @param deltaFunction The delta function to use
     * @param stateSerializer TypeSerializer for the data elements.
     *
     * @param <W> The type of {@link Window Windows} on which this trigger can operate.
     */
    public static <W extends Window> CustomTrigger<W> of(double threshold, DeltaFunction<AlarmRecord> deltaFunction, TypeSerializer<AlarmRecord> stateSerializer) {
        return new CustomTrigger<>(threshold, deltaFunction, stateSerializer);
    }
}

package falseresync.vivatech.client.wire;

import falseresync.vivatech.common.power.wire.Wire;

public interface WireParameters {
    WireModel getModel();

    float getSaggedYForSegment(float unsaggedY, float segmentNo);

    default float getSaggedYForX(float unsaggedY, float x) {
        return getSaggedYForSegment(unsaggedY, x / getModel().getSegmentSize());
    }

    @FunctionalInterface
    interface Factory {
        WireParameters build(Wire wire, WireModel model);
    }
}

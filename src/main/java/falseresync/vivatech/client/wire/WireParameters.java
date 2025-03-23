package falseresync.vivatech.client.wire;

import falseresync.vivatech.common.power.wire.Wire;

public interface WireParameters {
    WireModel getModel();

    float getSaggedY(int segmentNo, float yStep);

    @FunctionalInterface
    interface Factory {
        WireParameters build(Wire wire, WireModel model);
    }
}

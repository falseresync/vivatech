package falseresync.vivatech.client.wire;

import falseresync.vivatech.client.wire.WireModel;
import falseresync.vivatech.client.wire.WireParameters;
import falseresync.vivatech.common.power.wire.Wire;

public abstract class SimpleWireParameters implements WireParameters {
    private final Wire wire;
    private final falseresync.vivatech.client.wire.WireModel parameters;
    private final float k1;
    private final float k2;

    public SimpleWireParameters(Wire wire, falseresync.vivatech.client.wire.WireModel parameters) {
        this.wire = wire;
        this.parameters = parameters;
        k1 = (float) (getSaggingCoefficient() * 4f * Math.pow(parameters.getSegmentLength() / wire.length(), 2));
        k2 = getSaggingCoefficient() * 4f * parameters.getSegmentLength() / wire.length();
    }

    @Override
    public WireModel getModel() {
        return parameters;
    }

    @Override
    public float getSaggedYForSegment(float unsaggedY, float segmentNo) {
        return unsaggedY + k1 * segmentNo * segmentNo - k2 * segmentNo;
    }

    public abstract float getSaggingCoefficient();
}

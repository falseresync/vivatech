package falseresync.vivatech.client.wire;

import falseresync.vivatech.common.power.wire.Wire;

public abstract class DefaultRenderableWire implements WireParameters {
    private final Wire wire;
    private final WireModel parameters;

    protected DefaultRenderableWire(Wire wire, WireModel parameters) {
        this.wire = wire;
        this.parameters = parameters;
    }

    @Override
    public WireModel getModel() {
        return parameters;
    }

    @Override
    public float getSaggedY(int segmentNo, float yStep) {
        return (float) (yStep * segmentNo + getSaggingCoefficient() * (Math.pow(2 * (parameters.getSegmentSize() * segmentNo) - wire.length(), 2) / Math.pow(wire.length(), 2) - 1));
    }

    public abstract float getSaggingCoefficient();
}

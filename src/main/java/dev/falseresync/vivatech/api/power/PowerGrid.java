package dev.falseresync.vivatech.api.power;

import java.util.Set;

public abstract class PowerGrid {
    public abstract boolean isRemoved();

    public abstract void markRemoved();

    protected abstract void merge(PowerGrid other);

    public abstract void addNode(PowerGridNode node);

    public abstract void removeNode(PowerGridNode node);

    public abstract Set<PowerGridNode> getNodes();

    public abstract PowerGridState getState();
}

package dev.falseresync.vivatech.common.power;

import dev.falseresync.vivatech.api.power.PowerGrid;
import dev.falseresync.vivatech.api.power.PowerGridNode;
import dev.falseresync.vivatech.api.power.PowerGridState;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class VivatechPowerGrid extends PowerGrid {
    protected final UUID id = UUID.randomUUID();
    protected final ObjectSet<PowerGridNode> nodes;
    protected long load = 0;
    protected long generation = 0;
    protected int voltage = 0;
    protected boolean removed = false;

    public VivatechPowerGrid(Collection<PowerGridNode> nodes) {
        this.nodes = new ObjectOpenHashSet<>(nodes.size());
        this.nodes.addAll(nodes);
    }

    @Override
    public boolean isRemoved() {
        return removed;
    }

    @Override
    public void markRemoved() {
        removed = true;
    }

    @Override
    protected void merge(PowerGrid other) {
        nodes.addAll(other.getNodes());
        for (PowerGridNode node : other.getNodes()) {
            node.setPowerGrid(this);
        }
        other.markRemoved();
    }

    @Override
    public void addNode(PowerGridNode node) {
        node.getPowerGrid().ifPresentOrElse(
                this::merge,
                () -> {
                    nodes.add(node);
                    node.setPowerGrid(this);
                });
    }

    @Override
    public void removeNode(PowerGridNode node) {
        nodes.remove(node);
        node.setPowerGrid(null);
    }

    @Override
    public Set<PowerGridNode> getNodes() {
        return new ObjectOpenHashSet<>(nodes);
    }

    @Override
    public PowerGridState getState() {
        return new PowerGridState(load, generation, voltage);
    }

    /**
     * Check whether a given node belongs to any grid,
     * <ul>
     *  <li>remove it if it belongs to another grid</li>
     *  <li>update it if it doesn't belong to any grid</li>
     * </ul>
     *
     * @return whether a given node is still in this grid
     */
    protected boolean validateNodeAttachment(PowerGridNode node) {
        var nodeGrid = node.getPowerGrid();
        if (nodeGrid.isPresent() && !nodeGrid.get().equals(this)) {
            nodes.remove(node);
            return false;
        } else if (nodeGrid.isEmpty()) {
            node.setPowerGrid(this);
        }

        return true;
    }

    /**
     * Firstly, queries all nodes loads and generations and updates the grid values
     * <p>
     * Secondly, calculates a new voltage based on an average value between all nodes multiplied by the power balance
     */
    protected void updateState() {
        long voltagesSum = 0;
        long load = 0;
        long generation = 0;
        for (PowerGridNode node : nodes) {
            if (validateNodeAttachment(node)) {
                voltagesSum += node.getDesiredVoltage();
                load += node.getPowerLoad();
                generation += node.getPowerGeneration();
            }
        }

        this.load = load;
        this.generation = generation;
        var powerBalance = load > 0 ? (double) generation / load : 1;
        voltage = (int) ((voltagesSum / nodes.size()) * powerBalance);
    }

    public void tick() {
        if (removed) return;

        updateState();

        if (nodes.isEmpty()) {
            markRemoved();
            return;
        }

        for (PowerGridNode node : nodes) {
            node.powerGridTick(voltage);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VivatechPowerGrid powerGrid = (VivatechPowerGrid) o;
        return Objects.equals(id, powerGrid.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

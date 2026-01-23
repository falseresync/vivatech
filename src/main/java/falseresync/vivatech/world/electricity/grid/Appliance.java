package falseresync.vivatech.world.electricity.grid;

import net.minecraft.core.BlockPos;

public interface Appliance {
    /**
     * Must not change
     */
    BlockPos getAppliancePos();

    /**
     * Only called when first connected. <br/>
     * Does not get called when a grid changes through mergers and partitions.
     */
    default void onGridConnected() {
    }

    /**
     * Only called when completely disconnected. <br/>
     * Does not get called when a grid changes through mergers and partitions.
     */
    default void onGridDisconnected() {
    }

    /**
     * Positive current corresponds to power generation, negative current - to consumption
     */
    default float getElectricalCurrent() {
        return 0;
    }

    /**
     * It is advisable to minimize the cost of this method. Do expensive calculations in the regular tick methods
     */
    default void gridTick(float voltage) {
    }

    default void onGridFrozen() {
    }

    default void onGridUnfrozen() {
    }
}

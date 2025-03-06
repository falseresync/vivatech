package falseresync.vivatech.common.power;

import net.minecraft.util.math.BlockPos;

public interface Appliance {
    /**
     * Must not change
     */
    BlockPos getPos();

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

    default void gridTick(float voltage) {
    }
}

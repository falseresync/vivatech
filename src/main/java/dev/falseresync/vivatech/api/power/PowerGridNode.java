package dev.falseresync.vivatech.api.power;

import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Range;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface PowerGridNode {
    BlockPos getPos();

    @Range(from = 1, to = Integer.MAX_VALUE)
    int getDesiredVoltage();

    @Range(from = 0, to = Integer.MAX_VALUE)
    default int getPowerLoad() {
        return 0;
    }

    @Range(from = 0, to = Integer.MAX_VALUE)
    default int getPowerGeneration() {
        return 0;
    }

    default void powerGridTick(int voltage) {}

    Optional<PowerGrid> getPowerGrid();

    @ApiStatus.OverrideOnly
    void setPowerGrid(@Nullable PowerGrid powerGrid);
}

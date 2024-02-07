package dev.falseresync.vivatech.api.power;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public interface PowerGridNodeProvider {
    @Nullable
    PowerGridNode getPowerGridNode(World world, BlockPos pos);
}

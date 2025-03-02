package falseresync.lib.extras;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings("NonExtendableApiUsage")
public interface ContextlessBlockApiLookup<A> extends BlockApiLookup<A, Void> {
    default A find(World world, BlockPos pos) {
        return find(world, pos, null, null, null);
    }
}

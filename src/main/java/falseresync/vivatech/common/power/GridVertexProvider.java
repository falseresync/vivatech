package falseresync.vivatech.common.power;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface GridVertexProvider {
    GridVertex getGridVertex(World world, BlockPos pos, BlockState state);
}

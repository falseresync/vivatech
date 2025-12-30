package falseresync.vivatech.common.power.grid;

import falseresync.vivatech.common.power.grid.GridVertex;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface GridVertexProvider {
    GridVertex getGridVertex(Level world, BlockPos pos, BlockState state);
}

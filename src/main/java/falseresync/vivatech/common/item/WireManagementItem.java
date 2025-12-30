package falseresync.vivatech.common.item;

import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.power.grid.GridVertex;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import falseresync.vivatech.common.power.PowerSystem;

public abstract class WireManagementItem extends Item {
    public WireManagementItem(Properties settings) {
        super(settings);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player miner) {
        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var world = context.getLevel();

        var currentPos = context.getClickedPos();
        if (context.getPlayer() == null || !context.getPlayer().mayInteract(context.getLevel(), currentPos)) {
            return InteractionResult.FAIL;
        }

        var currentVertex = PowerSystem.GRID_VERTEX.find(world, currentPos, null);
        if (currentVertex == null) {
            return InteractionResult.FAIL;
        }

        var stack = context.getItemInHand();
        var connection = stack.get(VivatechComponents.CONNECTION);
        if (connection == null) {
            stack.set(VivatechComponents.CONNECTION, new GlobalPos(world.dimension(), currentPos));
            return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
        }

        if (connection.dimension() != world.dimension() || connection.pos().equals(currentPos)) {
            return InteractionResult.FAIL;
        }

        var previousVertex = PowerSystem.GRID_VERTEX.find(world, connection.pos(), null);
        if (previousVertex != null) {
            stack.remove(VivatechComponents.CONNECTION);
            return manageWire(context, connection, previousVertex, currentVertex);
        }

        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }

    protected abstract InteractionResult manageWire(UseOnContext context, GlobalPos connection, GridVertex vertexU, GridVertex vertexV);
}

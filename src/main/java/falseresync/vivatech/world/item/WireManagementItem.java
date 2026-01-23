package falseresync.vivatech.world.item;

import falseresync.vivatech.world.component.VivatechComponents;
import falseresync.vivatech.world.electricity.PowerSystemsManager;
import falseresync.vivatech.world.electricity.grid.GridVertex;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class WireManagementItem extends Item {
    public WireManagementItem(Properties settings) {
        super(settings);
    }

    @Override
    public boolean mineBlock(ItemStack itemStack, Level level, BlockState state, BlockPos pos, LivingEntity owner) {
        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        var level = context.getLevel();

        var currentPos = context.getClickedPos();
        if (context.getPlayer() == null || !context.getLevel().mayInteract(context.getPlayer(), currentPos)) {
            return InteractionResult.FAIL;
        }

        var currentVertex = PowerSystemsManager.GRID_VERTICES.find(level, currentPos, null);
        if (currentVertex == null) {
            return InteractionResult.FAIL;
        }

        var stack = context.getItemInHand();
        var connection = stack.get(VivatechComponents.CONNECTION);
        if (connection == null) {
            stack.set(VivatechComponents.CONNECTION, new GlobalPos(level.dimension(), currentPos));
            return InteractionResult.SUCCESS;
        }

        if (connection.dimension() != level.dimension() || connection.pos().equals(currentPos)) {
            return InteractionResult.FAIL;
        }

        var previousVertex = PowerSystemsManager.GRID_VERTICES.find(level, connection.pos(), null);
        if (previousVertex != null) {
            stack.remove(VivatechComponents.CONNECTION);
            return manageWire(context, connection, previousVertex, currentVertex);
        }

        return InteractionResult.SUCCESS;
    }

    protected abstract InteractionResult manageWire(UseOnContext context, GlobalPos connection, GridVertex vertexU, GridVertex vertexV);
}

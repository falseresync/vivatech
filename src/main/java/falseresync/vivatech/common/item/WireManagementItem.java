package falseresync.vivatech.common.item;

import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.power.GridVertex;
import falseresync.vivatech.common.power.PowerSystem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

public abstract class WireManagementItem extends Item {
    public WireManagementItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        var world = context.getWorld();

        var currentPos = context.getBlockPos();
        var currentVertex = PowerSystem.GRID_VERTEX.find(world, currentPos, null);
        if (currentVertex == null) {
            return ActionResult.FAIL;
        }

        var stack = context.getStack();
        var connection = stack.get(VivatechComponents.CONNECTION);
        if (connection == null) {
            stack.set(VivatechComponents.CONNECTION, new GlobalPos(world.getRegistryKey(), currentPos));
            return ActionResult.success(context.getWorld().isClient);
        }

        if (connection.dimension() != world.getRegistryKey() || connection.pos().equals(currentPos)) {
            return ActionResult.FAIL;
        }

        var previousVertex = PowerSystem.GRID_VERTEX.find(world, connection.pos(), null);
        if (previousVertex != null) {
            stack.remove(VivatechComponents.CONNECTION);
            return manageWire(context, previousVertex, currentVertex);
        }

        return ActionResult.success(context.getWorld().isClient);
    }

    protected abstract ActionResult manageWire(ItemUsageContext context, GridVertex vertexU, GridVertex vertexV);
}

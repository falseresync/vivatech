package falseresync.vivatech.common.item;

import falseresync.vivatech.common.data.VtComponents;
import falseresync.vivatech.common.power.GridVertex;
import falseresync.vivatech.common.power.PowerSystem;
import net.minecraft.item.Item;
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
        var connection = stack.get(VtComponents.CONNECTION);
        if (connection == null) {
            stack.set(VtComponents.CONNECTION, new GlobalPos(world.getRegistryKey(), currentPos));
            return ActionResult.success(context.getWorld().isClient);
        }

        if (connection.dimension() != world.getRegistryKey()) {
            return ActionResult.FAIL;
        }

        var previousVertex = PowerSystem.GRID_VERTEX.find(world, connection.pos(), null);
        if (previousVertex != null) {
            stack.remove(VtComponents.CONNECTION);
            return manageWire(world, previousVertex, currentVertex);
        }

        return ActionResult.success(context.getWorld().isClient);
    }

    protected abstract ActionResult manageWire(World world, GridVertex vertexU, GridVertex vertexV);
}

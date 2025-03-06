package falseresync.vivatech.common.item;

import falseresync.vivatech.common.data.VtComponents;
import falseresync.vivatech.common.power.GridNode;
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
        var currentNode = PowerSystem.GRID_NODE.find(world, currentPos, null);
        if (currentNode == null) {
            return ActionResult.FAIL;
        }

        var stack = context.getStack();
        var previous = stack.get(VtComponents.CONNECTION);
        if (previous == null) {
            stack.set(VtComponents.CONNECTION, new GlobalPos(world.getRegistryKey(), currentPos));
            return ActionResult.success(context.getWorld().isClient);
        }

        if (previous.dimension() != world.getRegistryKey()) {
            return ActionResult.FAIL;
        }

        var previousNode = PowerSystem.GRID_NODE.find(world, previous.pos(), null);
        if (previousNode != null) {
            stack.remove(VtComponents.CONNECTION);
            return manageWire(world, previousNode, currentNode);
        }

        return ActionResult.success(context.getWorld().isClient);
    }

    protected abstract ActionResult manageWire(World world, GridNode nodeU, GridNode nodeV);
}

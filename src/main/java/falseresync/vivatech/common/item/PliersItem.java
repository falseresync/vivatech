package falseresync.vivatech.common.item;

import falseresync.vivatech.common.power.PowerNode;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;


public class PliersItem extends WireManagementItem {
    public PliersItem(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult manageWire(World world, PowerNode previousNode, PowerNode currentNode, GlobalPos previous, BlockPos currentPos) {
        if (!world.isClient) {
            var previousSystem = previousNode.getPowerSystem();
            var currentSystem = currentNode.getPowerSystem();
            // equals automatically checks for nullity
            if (previousSystem == null || !previousSystem.equals(currentSystem)) {
                return ActionResult.FAIL;
            }

            currentSystem.cut(previous.pos(), currentPos);
            return ActionResult.CONSUME;
        }

        return ActionResult.SUCCESS;
    }
}

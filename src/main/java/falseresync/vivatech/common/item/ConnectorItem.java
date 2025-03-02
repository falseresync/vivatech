package falseresync.vivatech.common.item;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.power.PowerNode;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

public class ConnectorItem extends WireManagementItem {
    public ConnectorItem(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult manageWire(World world, PowerNode previousNode, PowerNode currentNode, GlobalPos previous, BlockPos currentPos) {
        if (!world.isClient) {
            var previousSystem = previousNode.getPowerSystem();
            if (previousSystem != null) {
                previousSystem.add(previous.pos(), currentPos);
            } else {
                var currentSystem = currentNode.getPowerSystem();
                if (currentSystem != null) {
                    currentSystem.add(currentPos, previous.pos());
                } else {
                    var newSystem = Vivatech.getPowerSystemsManager().create(world);
                    newSystem.add(previous.pos(), currentPos);
                }
            }

            return ActionResult.CONSUME;
        }

        return ActionResult.SUCCESS;
    }
}

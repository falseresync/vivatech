package falseresync.vivatech.common.item;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.power.GridNode;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class ConnectorItem extends WireManagementItem {
    public ConnectorItem(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult manageWire(World world, GridNode nodeU, GridNode nodeV) {
        if (!world.isClient) {
            var gridsManager = Vivatech.getServerGridsLoader().getGridsManager(world);
            var grid = gridsManager.findOrCreate(nodeU.pos(), nodeV.pos());
            return grid.connect(nodeU, nodeV) ? ActionResult.CONSUME : ActionResult.FAIL;
        }

        return ActionResult.SUCCESS;
    }
}

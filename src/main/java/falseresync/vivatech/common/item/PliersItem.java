package falseresync.vivatech.common.item;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.power.GridNode;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class PliersItem extends WireManagementItem {
    public PliersItem(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult manageWire(World world, GridNode nodeU, GridNode nodeV) {
        if (!world.isClient) {
            var gridsManager = Vivatech.getServerGridsLoader().getGridsManager(world);
            var grid = gridsManager.find(nodeU.pos(), nodeV.pos());
            if (grid == null) {
                return ActionResult.FAIL;
            }
            return grid.cut(nodeU.pos(), nodeV.pos()) ? ActionResult.CONSUME : ActionResult.FAIL;
        }

        return ActionResult.SUCCESS;
    }
}

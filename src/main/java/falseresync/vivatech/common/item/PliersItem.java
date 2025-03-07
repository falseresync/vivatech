package falseresync.vivatech.common.item;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.power.GridVertex;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class PliersItem extends WireManagementItem {
    public PliersItem(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult manageWire(World world, GridVertex vertexU, GridVertex vertexV) {
        if (!world.isClient) {
            var gridsManager = Vivatech.getServerGridsLoader().getGridsManager(world);
            var grid = gridsManager.find(vertexU.pos(), vertexV.pos());
            if (grid == null) {
                return ActionResult.FAIL;
            }
            return grid.disconnect(vertexU, vertexV) ? ActionResult.CONSUME : ActionResult.FAIL;
        }

        return ActionResult.SUCCESS;
    }
}

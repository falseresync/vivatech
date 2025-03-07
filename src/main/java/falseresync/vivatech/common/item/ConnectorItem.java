package falseresync.vivatech.common.item;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.power.GridVertex;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class ConnectorItem extends WireManagementItem {
    public ConnectorItem(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult manageWire(World world, GridVertex vertexU, GridVertex vertexV) {
        if (!world.isClient) {
            var gridsManager = Vivatech.getServerGridsLoader().getGridsManager(world);
            var grid = gridsManager.findOrCreate(vertexU.pos(), vertexV.pos());
            return grid.connect(vertexU, vertexV) ? ActionResult.CONSUME : ActionResult.FAIL;
        }

        return ActionResult.SUCCESS;
    }
}

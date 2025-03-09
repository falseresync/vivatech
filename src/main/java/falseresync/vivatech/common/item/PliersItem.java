package falseresync.vivatech.common.item;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.power.GridVertex;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.world.World;

public class PliersItem extends WireManagementItem {
    public PliersItem(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult manageWire(ItemUsageContext context, GridVertex vertexU, GridVertex vertexV) {
        if (!context.getWorld().isClient) {
            var gridsManager = Vivatech.getServerGridsLoader().getGridsManager(context.getWorld());
            var grid = gridsManager.find(vertexU.pos(), vertexV.pos());
            if (grid == null) {
                return ActionResult.FAIL;
            }
            return grid.disconnect(vertexU, vertexV) ? ActionResult.CONSUME : ActionResult.FAIL;
        }

        return ActionResult.SUCCESS;
    }
}

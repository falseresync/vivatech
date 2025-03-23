package falseresync.vivatech.common.item;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.power.grid.GridVertex;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.GlobalPos;

public class PliersItem extends WireManagementItem {
    public PliersItem(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult manageWire(ItemUsageContext context, GlobalPos connection, GridVertex vertexU, GridVertex vertexV) {
        if (context.getPlayer() instanceof ServerPlayerEntity) {
            var grid = Vivatech.getPowerSystem().in(context.getWorld().getRegistryKey()).find(vertexU.pos(), vertexV.pos());
            if (grid == null) {
                return ActionResult.FAIL;
            }

            if (grid.disconnect(vertexU, vertexV)) {
                return ActionResult.SUCCESS;
            }

            return ActionResult.FAIL;
        }

        return ActionResult.CONSUME;
    }
}

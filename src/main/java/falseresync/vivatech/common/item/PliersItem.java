package falseresync.vivatech.common.item;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.power.GridVertex;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;

public class PliersItem extends WireManagementItem {
    public PliersItem(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult manageWire(ItemUsageContext context, GlobalPos connection, GridVertex vertexU, GridVertex vertexV) {
        if (context.getPlayer() instanceof ServerPlayerEntity player) {
            var grid = Vivatech.getPowerSystem().in(context.getWorld().getRegistryKey())
                    .find(vertexU.pos(), vertexV.pos());
            if (grid == null) {
                return ActionResult.FAIL;
            }

            if (grid.disconnect(vertexU, vertexV)) {
                player.getInventory().offerOrDrop(new ItemStack(VivatechItems.WIRE, MathHelper.floor(vertexU.pos().subtract(vertexV.pos()).toCenterPos().length())));
                return ActionResult.SUCCESS;
            }

            return ActionResult.FAIL;
        }

        return ActionResult.CONSUME;
    }
}

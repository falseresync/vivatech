package falseresync.vivatech.world.item;

import falseresync.vivatech.Vivatech;
import falseresync.vivatech.world.electricity.grid.GridVertex;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class PliersItem extends WireManagementItem {
    public PliersItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    protected InteractionResult manageWire(UseOnContext context, GlobalPos connection, GridVertex vertexU, GridVertex vertexV) {
        if (context.getPlayer() instanceof ServerPlayer) {
            var grid = Vivatech.getPowerSystemsManager().getFor(context.getLevel().dimension()).find(vertexU.pos(), vertexV.pos());
            if (grid == null) {
                return InteractionResult.FAIL;
            }

            if (grid.disconnect(vertexU, vertexV)) {
                return InteractionResult.SUCCESS;
            }

            return InteractionResult.FAIL;
        }

        return InteractionResult.CONSUME;
    }
}

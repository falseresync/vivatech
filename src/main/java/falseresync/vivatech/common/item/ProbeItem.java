package falseresync.vivatech.common.item;

import falseresync.vivatech.common.Vivatech;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ProbeItem extends Item {
    public ProbeItem(Properties settings) {
        super(settings);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player miner) {
        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() instanceof ServerPlayer player) {
            var grid = Vivatech.getPowerSystem().in(context.getLevel().dimension()).getGridLookup().get(context.getClickedPos());
            if (grid != null) {
                player.displayClientMessage(Component.translatable("hud.vivatech.probe", Math.round(grid.getLastVoltage()), grid.getLastCurrent()), true);
            }
        }
        return super.useOn(context);
    }
}

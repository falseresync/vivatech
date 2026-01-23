package falseresync.vivatech.world.item;

import falseresync.vivatech.Vivatech;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class ProbeItem extends Item {
    public ProbeItem(Properties settings) {
        super(settings);
    }


    @Override
    public boolean mineBlock(ItemStack itemStack, Level level, BlockState state, BlockPos pos, LivingEntity owner) {
        return false;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        if (context.getPlayer() instanceof ServerPlayer player) {
            var grid = Vivatech.getPowerSystemsManager().getFor(context.getLevel().dimension()).getGridLookup().get(context.getClickedPos());
            if (grid != null) {
                if (grid.isFrozen()) {
                    player.displayClientMessage(Component.translatable("hud.vivatech.probe.grid_frozen").withStyle(ChatFormatting.BOLD).withStyle(ChatFormatting.DARK_RED), true);
                } else {
                    player.displayClientMessage(Component.translatable("hud.vivatech.probe.grid_parameters", Math.round(grid.getLastVoltage()), grid.getLastCurrent()), true);
                }
            }
        }
        return super.useOn(context);
    }
}

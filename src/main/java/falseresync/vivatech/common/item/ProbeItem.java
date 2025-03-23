package falseresync.vivatech.common.item;

import falseresync.vivatech.common.Vivatech;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ProbeItem extends Item {
    public ProbeItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return false;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getPlayer() instanceof ServerPlayerEntity player) {
            var grid = Vivatech.getPowerSystem().in(context.getWorld().getRegistryKey()).getGridLookup().get(context.getBlockPos());
            if (grid != null) {
                player.sendMessage(Text.translatable("hud.vivatech.probe", Math.round(grid.getLastVoltage()), grid.getLastCurrent()), true);
            }
        }
        return super.useOnBlock(context);
    }
}

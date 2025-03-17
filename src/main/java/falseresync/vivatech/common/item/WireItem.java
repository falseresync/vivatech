package falseresync.vivatech.common.item;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.data.ItemBarComponent;
import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.power.GridVertex;
import falseresync.vivatech.common.power.wire.WireType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Colors;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class WireItem extends WireManagementItem {
    public WireItem(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult manageWire(ItemUsageContext context, GlobalPos connection, GridVertex vertexU, GridVertex vertexV) {
        if (context.getPlayer() instanceof ServerPlayerEntity player) {
            var stack = context.getStack();
            if (!connection.pos().isWithinDistance(player.getPos(), stack.getCount() * 5D/4)) {
                stack.remove(VivatechComponents.ITEM_BAR);
                player.dropItem(stack.copy(), true);
                player.getInventory().dropSelectedItem(true);
                return ActionResult.FAIL;
            }

            var grid = Vivatech.getPowerSystem().in(context.getWorld().getRegistryKey())
                    .findOrCreate(vertexU.pos(), vertexV.pos(), WireType.V_230);
            if (grid.getWireType() != WireType.V_230) {
                return ActionResult.FAIL;
            }

            if (grid.connect(vertexU, vertexV)) {
                stack.remove(VivatechComponents.ITEM_BAR);
                stack.decrement(MathHelper.ceil(vertexU.pos().subtract(vertexV.pos()).toCenterPos().length()));
                player.setStackInHand(context.getHand(), stack);
                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.CONSUME;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient && entity instanceof PlayerEntity player) {
            var connection = stack.get(VivatechComponents.CONNECTION);
            if (connection == null) {
                return;
            }

            if (!ItemStack.areEqual(player.getMainHandStack(), stack)
                    || connection.dimension() != world.getRegistryKey()) {
                stack.remove(VivatechComponents.CONNECTION);
                stack.remove(VivatechComponents.ITEM_BAR);
                return;
            }

            stack.set(VivatechComponents.ITEM_BAR, new ItemBarComponent(
                    MathHelper.clamp(Math.round(13 - (float) (connection.pos().getSquaredDistance(player.getPos()) * 13 / Math.pow(stack.getCount(), 2))), 1, 13),
                    Colors.WHITE
            ));
        }
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return stack.contains(VivatechComponents.ITEM_BAR);
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        return stack.getOrDefault(VivatechComponents.ITEM_BAR, ItemBarComponent.DEFAULT).step();
    }
}

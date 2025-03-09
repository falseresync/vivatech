package falseresync.vivatech.common.item;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.data.ItemBarComponent;
import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.power.GridVertex;
import falseresync.vivatech.common.power.WireType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Colors;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class WireItem extends WireManagementItem {
    public WireItem(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult manageWire(ItemUsageContext context, GridVertex vertexU, GridVertex vertexV) {
        if (!context.getWorld().isClient) {
            var gridsManager = Vivatech.getServerGridsLoader().getGridsManager(context.getWorld());
            var grid = gridsManager.findOrCreate(vertexU.pos(), vertexV.pos(), WireType.V_230);
            if (grid.getWireType() != WireType.V_230) {
                return ActionResult.FAIL;
            }
            if (grid.connect(vertexU, vertexV)) {
                context.getStack().remove(VivatechComponents.ITEM_BAR);
                context.getStack().decrement((int) (vertexU.pos().subtract(vertexV.pos()).toBottomCenterPos().length() * 4D/5));
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

            if (!connection.pos().isWithinDistance(player.getPos(), stack.getCount() * 5D/4)) {
                stack.remove(VivatechComponents.CONNECTION);
                stack.remove(VivatechComponents.ITEM_BAR);
                player.getInventory().removeStack(player.getInventory().getSlotWithStack(stack));
                var middle = player.getPos().add(connection.pos().toCenterPos()).multiply(0.5f);
                ItemScatterer.spawn(world, middle.x, middle.y, middle.z, stack);
                return;
            }

            stack.set(VivatechComponents.ITEM_BAR, new ItemBarComponent(
                    MathHelper.clamp(Math.round(13 - (float) (connection.pos().getSquaredDistance(player.getPos()) * 13 / Math.pow(stack.getCount() * 5D/4, 2))), 0, 13),
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

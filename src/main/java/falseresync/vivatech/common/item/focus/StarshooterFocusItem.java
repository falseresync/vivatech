package falseresync.vivatech.common.item.focus;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.entity.StarProjectileEntity;
import falseresync.vivatech.common.Reports;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class StarshooterFocusItem extends FocusItem {
    public StarshooterFocusItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> focusUse(ItemStack gadgetStack, ItemStack focusStack, World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity player) {
            if (Vivatech.getChargeManager().tryExpendGadgetCharge(gadgetStack, 2, user)) {
                world.spawnEntity(new StarProjectileEntity(user, world));
                focusStack.damage(1, user, EquipmentSlot.MAINHAND);
                return TypedActionResult.success(gadgetStack);
            }

            Reports.insufficientCharge(player);
            return TypedActionResult.fail(gadgetStack);
        }

        return super.focusUse(gadgetStack, focusStack, world, user, hand);
    }
}

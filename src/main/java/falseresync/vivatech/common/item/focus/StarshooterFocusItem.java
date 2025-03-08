package falseresync.vivatech.common.item.focus;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.entity.StarProjectileEntity;
import falseresync.vivatech.network.report.VivatechReports;
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
    public TypedActionResult<ItemStack> focusUse(ItemStack wandStack, ItemStack focusStack, World world, PlayerEntity user, Hand hand) {
        if (user instanceof ServerPlayerEntity player) {
            if (Vivatech.getChargeManager().tryExpendWandCharge(wandStack, 2, user)) {
                world.spawnEntity(new StarProjectileEntity(user, world));
                focusStack.damage(1, user, EquipmentSlot.MAINHAND);
                return TypedActionResult.success(wandStack);
            }

            VivatechReports.WAND_INSUFFICIENT_CHARGE.sendTo(player);
            return TypedActionResult.fail(wandStack);
        }

        return super.focusUse(wandStack, focusStack, world, user, hand);
    }
}

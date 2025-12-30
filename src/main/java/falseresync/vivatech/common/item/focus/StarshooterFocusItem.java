package falseresync.vivatech.common.item.focus;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.entity.StarProjectileEntity;
import falseresync.vivatech.common.item.focus.FocusItem;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import falseresync.vivatech.common.Reports;

public class StarshooterFocusItem extends FocusItem {
    public StarshooterFocusItem(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> focusUse(ItemStack gadgetStack, ItemStack focusStack, Level world, Player user, InteractionHand hand) {
        if (user instanceof ServerPlayer player) {
            if (Vivatech.getChargeManager().tryExpendGadgetCharge(gadgetStack, 2, user)) {
                world.addFreshEntity(new StarProjectileEntity(user, world));
                focusStack.hurtAndBreak(1, user, EquipmentSlot.MAINHAND);
                return InteractionResultHolder.success(gadgetStack);
            }

            Reports.insufficientCharge(player);
            return InteractionResultHolder.fail(gadgetStack);
        }

        return super.focusUse(gadgetStack, focusStack, world, user, hand);
    }
}

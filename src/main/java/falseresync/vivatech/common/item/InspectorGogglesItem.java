package falseresync.vivatech.common.item;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketItem;
import falseresync.vivatech.common.data.VivatechAttachments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public class InspectorGogglesItem extends TrinketItem {
    public InspectorGogglesItem(Settings settings) {
        super(settings);
    }

    @Override
    public void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onEquip(stack, slot, entity);
        if (entity instanceof PlayerEntity player) {
            player.setAttached(VivatechAttachments.HAS_INSPECTOR_GOGGLES, true);
        }
    }

    @Override
    public void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        super.onUnequip(stack, slot, entity);
        if (entity instanceof PlayerEntity player) {
            player.removeAttached(VivatechAttachments.HAS_INSPECTOR_GOGGLES);
        }
    }
}

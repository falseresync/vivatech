package falseresync.vivatech.lifessence;

import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.List;

public interface LifessenceStoringItem {
    LifessenceStorage getLifessenceStorage(ContainerItemContext context);

    default void appendLifessenceTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        var storage = getLifessenceStorage(ContainerItemContext.withConstant(stack));
        tooltip.add(Text.translatable("tooltip.vivatech.lifessence.amount", storage.getAmount(), storage.getCapacity()));
    }
}

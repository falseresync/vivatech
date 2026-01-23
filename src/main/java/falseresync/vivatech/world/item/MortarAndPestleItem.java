package falseresync.vivatech.world.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import org.jspecify.annotations.Nullable;

public class MortarAndPestleItem extends Item {
    public MortarAndPestleItem(Properties settings) {
        super(settings);
    }

    @Override
    public @Nullable ItemStackTemplate getCraftingRemainder(ItemStack stack) {
        var damage = stack.getDamageValue();
        if (damage + 1 == stack.getMaxDamage()) {
            return null;
        } else {
            stack.setDamageValue(damage + 1);
            return ItemStackTemplate.fromNonEmptyStack(stack);
        }
    }
}

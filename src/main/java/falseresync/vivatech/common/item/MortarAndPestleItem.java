package falseresync.vivatech.common.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class MortarAndPestleItem extends Item {
    public MortarAndPestleItem(Properties settings) {
        super(settings);
    }

    @Override
    public ItemStack getRecipeRemainder(ItemStack stack) {
        var damage = stack.getDamageValue();
        if (damage + 1 == stack.getMaxDamage()) {
            return ItemStack.EMPTY;
        } else {
            stack.setDamageValue(damage + 1);
            return stack.copy();
        }
    }
}

package falseresync.vivatech.common.data;

import net.minecraft.item.ItemStack;

public interface InventoryComponentProvider {
    int getDefaultInventorySize();

    default int getInventorySize(ItemStack stack) {
        return stack.getOrDefault(VivatechComponents.INVENTORY_SIZE, getDefaultInventorySize());
    }

    default InventoryComponent getOrCreateInventoryComponent(ItemStack stack) {
        return stack.getOrDefault(VivatechComponents.INVENTORY, InventoryComponent.createDefault(getInventorySize(stack)));
    }
}

package falseresync.vivatech.common.data;

import falseresync.vivatech.common.data.InventoryComponent;
import falseresync.vivatech.common.data.VivatechComponents;
import net.minecraft.world.item.ItemStack;

public interface InventoryComponentProvider {
    int getDefaultInventorySize();

    default int getInventorySize(ItemStack stack) {
        return stack.getOrDefault(falseresync.vivatech.common.data.VivatechComponents.INVENTORY_SIZE, getDefaultInventorySize());
    }

    default falseresync.vivatech.common.data.InventoryComponent getOrCreateInventoryComponent(ItemStack stack) {
        return stack.getOrDefault(VivatechComponents.INVENTORY, InventoryComponent.createDefault(getInventorySize(stack)));
    }
}

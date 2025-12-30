package falseresync.vivatech.common.data;

import falseresync.vivatech.common.data.InventoryComponent;
import falseresync.vivatech.common.data.VivatechComponents;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class EphemeralInventory extends SimpleContainer {
    private final falseresync.vivatech.common.data.InventoryComponent backingComponent;

    public EphemeralInventory(falseresync.vivatech.common.data.InventoryComponent backingComponent) {
        super(backingComponent.stacks().toArray(new ItemStack[backingComponent.size()]));
        this.backingComponent = backingComponent;
    }

    public falseresync.vivatech.common.data.InventoryComponent toImmutable() {
        return new InventoryComponent(items, backingComponent.size());
    }

    public void flush(ItemStack stack) {
        stack.set(VivatechComponents.INVENTORY, toImmutable());
    }

    @Override
    public void addListener(ContainerListener listener) {
        throw new UnsupportedOperationException();
    }
}

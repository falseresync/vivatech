package falseresync.vivatech.api.inventory;

import com.google.common.base.Predicates;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class SimpleInventoryBuilder extends InventoryBuilder<SimpleInventory> {
    protected Predicate<PlayerEntity> canPlayerUse = Predicates.alwaysTrue();

    @Override
    public SimpleInventoryBuilder addSlot(SlotDescription slot) {
        return (SimpleInventoryBuilder) super.addSlot(slot);
    }

    @Override
    public SimpleInventoryBuilder addSlots(SlotDescription... slots) {
        return (SimpleInventoryBuilder) super.addSlots(slots);
    }

    @Override
    public SimpleInventoryBuilder addOnDirty(Runnable onDirty) {
        return (SimpleInventoryBuilder) super.addOnDirty(onDirty);
    }

    public SimpleInventoryBuilder addCanPlayerUse(Predicate<PlayerEntity> canPlayerUse) {
        this.canPlayerUse = this.canPlayerUse.and(canPlayerUse);
        return this;
    }

    @Override
    public SimpleInventory build() {
        if (slots.isEmpty()) {
            return new SimpleInventory(0);
        }
        return new SimpleInventory(slots.size()) {
            @Override
            public ItemStack addStack(ItemStack stack) {
                for (int i = 0; i < slots.size(); i++) {
                    if (isValid(i, stack)) {
                        return true;
                    }
                }
                // TODO: maybe fuck SimpleInventory after all?
                return
            }

            @Override
            public boolean canInsert(ItemStack stack) {
                if (!super.canInsert(stack)) return false;
                for (int i = 0; i < slots.size(); i++) {
                    if (isValid(i, stack)) return true;
                }
                return false;
            }

            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return slots.get(slot).canInsert(stack);
            }

            @Override
            public boolean canTransferTo(Inventory hopperInventory, int slot, ItemStack stack) {
                return slots.get(slot).canExtract(stack);
            }

            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return canPlayerUse.test(player);
            }

            @Override
            public void markDirty() {
                super.markDirty();
                onDirty.forEach(Runnable::run);
            }
        };
    }
}

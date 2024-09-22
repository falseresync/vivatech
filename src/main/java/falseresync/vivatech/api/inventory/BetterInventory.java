package falseresync.vivatech.api.inventory;

import com.google.common.base.Preconditions;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

public interface BetterInventory extends Inventory {
    DefaultedList<ItemStack> getStacks();

    default boolean canInsert(int slot, ItemStack inserted) {
        Preconditions.checkArgument(slot >= 0 && slot < size());
        var existing = getStack(slot);
        return existing.isEmpty() && isValid(slot, inserted) ||
                ItemStack.areItemsAndComponentsEqual(existing, inserted) && existing.getCount() < existing.getMaxCount();
    }

    default boolean canInsert(ItemStack stack) {
        for (int slot = 0; slot < size(); slot++) {
            if (canInsert(slot, stack)) return true;
        }
        return false;
    }

    // Should first go to existing stacks, then to empty
    default ItemStack addStack(ItemStack stack) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        var inserted = stack.copy();
        // First go to existing stacks
        for (int slot = 0; slot < size(); slot++) {
            var existing = getStack(slot);
            if (existing.isEmpty()) continue;
            if (canInsert(slot, stack)) {
                int change = Math.min(inserted.getCount(), getMaxCount(existing) - existing.getCount());
                if (change > 0) {
                    existing.increment(change);
                    inserted.decrement(change);
                    markDirty();
                }
                if (inserted.isEmpty()) return ItemStack.EMPTY;
            }
        }
        // If no stacks exist, then go to the first empty slots
        for (int slot = 0; slot < size(); slot++) {
            if (canInsert(slot, stack)) {
                var existing = getStack(slot);
                if (existing.isEmpty()) {
                    int maxCount = getMaxCount(stack);
                    setStack(slot, inserted.copy());
                    inserted.decrement(maxCount);
                    if (inserted.isEmpty()) return ItemStack.EMPTY;
                }
            }
        }
        return inserted.isEmpty() ? ItemStack.EMPTY : inserted;
    }

    @Override
    default int size() {
        return getStacks().size();
    }

    @Override
    default boolean isEmpty() {
        for (ItemStack stack : getStacks()) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    default ItemStack getStack(int slot) {
        Preconditions.checkArgument(slot >= 0 && slot < size());
        return getStacks().get(slot);
    }

    @Override
    default ItemStack removeStack(int slot, int amount) {
        Preconditions.checkArgument(slot >= 0 && slot < size());
        var removedStack = Inventories.splitStack(getStacks(), slot, amount);
        if (!removedStack.isEmpty()) {
            markDirty();
        }

        return removedStack;
    }

    @Override
    default ItemStack removeStack(int slot) {
        Preconditions.checkArgument(slot >= 0 && slot < size());
        var removedStack = Inventories.removeStack(getStacks(), slot);
        if (!removedStack.isEmpty()) {
            markDirty();
        }

        return removedStack;
    }

    @Override
    default void setStack(int slot, ItemStack stack) {
        Preconditions.checkArgument(slot >= 0 && slot < size());
        Preconditions.checkArgument(isValid(slot, stack));
        getStacks().set(slot, stack);
        stack.capCount(getMaxCount(stack));
        markDirty();
    }

    @Override
    default void clear() {
        getStacks().clear();
        markDirty();
    }
}

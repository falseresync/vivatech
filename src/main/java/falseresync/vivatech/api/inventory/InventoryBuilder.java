package falseresync.vivatech.api.inventory;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import it.unimi.dsi.fastutil.ints.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public class InventoryBuilder {
    protected int size = 0;
    protected Int2ObjectMap<Predicate<ItemStack>> filters = new Int2ObjectRBTreeMap<>();
    protected Int2ObjectMap<IntSet> sides = new Int2ObjectRBTreeMap<>();

    public InventoryBuilder addSlot(@Nullable Predicate<ItemStack> filter) {
        filters.put(size, filter == null ? Predicates.alwaysTrue() : filter);
        size += 1;
        return this;
    }

    public InventoryBuilder openSideToSlots(Direction direction, int... sides) {
        this.sides.compute(direction.getId(), (key, oldValue) -> {
            if (oldValue == null) {
                return new IntArraySet(sides);
            } else {
                for (int side : sides) oldValue.add(side);
                return oldValue;
            }
        });
        return this;
    }

    public BetterInventory build() {
        return new BetterInventory() {
            private final DefaultedList<ItemStack> stacks = DefaultedList.ofSize(size, ItemStack.EMPTY);

            @Override
            public DefaultedList<ItemStack> getStacks() {
                return stacks;
            }

            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return filters.get(slot).test(stack);
            }

            @Override
            public void markDirty() {

            }

            @Override
            public boolean canPlayerUse(PlayerEntity player) {
                return true;
            }
        };
    }
}

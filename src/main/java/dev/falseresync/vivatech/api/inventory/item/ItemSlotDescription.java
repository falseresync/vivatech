package dev.falseresync.vivatech.api.inventory.item;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public interface ItemSlotDescription {
    boolean canInsert(ItemStack stack);

    boolean canExtract(ItemStack stack);

    int getIndex();

    static ItemSlotDescription of(int index, @Nullable Predicate<ItemStack> inputFilter, @Nullable Predicate<ItemStack> outputFilter) {
        Preconditions.checkArgument(index >= 0, "Slot indices must be non-negative");
        final Predicate<ItemStack> normalizedInputFilter = inputFilter == null ? Predicates.alwaysTrue() : inputFilter;
        final Predicate<ItemStack> normalizedOutputFilter = outputFilter == null ? Predicates.alwaysTrue() : outputFilter;
        return new ItemSlotDescription() {
            @Override
            public boolean canInsert(ItemStack stack) {
                return normalizedInputFilter.test(stack);
            }

            @Override
            public boolean canExtract(ItemStack stack) {
                return normalizedOutputFilter.test(stack);
            }

            @Override
            public int getIndex() {
                return index;
            }
        };
    }
}

package falseresync.vivatech.api.inventory;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface SlotDescription {
    boolean canInsert(ItemStack stack);

    boolean canExtract(ItemStack stack);

    int getIndex();

    static SlotDescription of(int index, @Nullable Predicate<ItemStack> inputFilter, @Nullable Predicate<ItemStack> outputFilter) {
        Preconditions.checkArgument(index >= 0, "Slot indices must be non-negative");
        final Predicate<ItemStack> normalizedInputFilter = inputFilter == null ? Predicates.alwaysTrue() : inputFilter;
        final Predicate<ItemStack> normalizedOutputFilter = outputFilter == null ? Predicates.alwaysTrue() : outputFilter;
        return new SlotDescription() {
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

    static SlotDescription of(int index) {
        return of(index, null, null);
    }
}
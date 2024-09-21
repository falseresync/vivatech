package falseresync.vivatech.api.inventory;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SidedInventoryBuilder extends InventoryBuilder<ImplementedSidedInventory> {
    protected final Int2ObjectMap<SlotDescription> slots = new Int2ObjectRBTreeMap<>();
    protected final Int2ObjectMap<Rule> insertionRules = new Int2ObjectRBTreeMap<>();
    protected final Int2ObjectMap<Rule> extractionRules = new Int2ObjectRBTreeMap<>();
    protected final ObjectSet<Runnable> onDirty = new ObjectArraySet<>();

    @Override
    public SidedInventoryBuilder addSlot(SlotDescription slot) {
        super.addSlot(slot);
        insertionRules.computeIfAbsent(slot.getIndex(), key -> (dir, stack) -> true);
        extractionRules.computeIfAbsent(slot.getIndex(), key -> (dir, stack) -> true);
        return this;
    }

    @Override
    public SidedInventoryBuilder addSlots(SlotDescription... slots) {
        return (SidedInventoryBuilder) super.addSlots(slots);
    }

    public SidedInventoryBuilder addInsertionRule(SlotDescription slot, Rule rule) {
        insertionRules.compute(slot.getIndex(), (key, oldValue) -> oldValue == null ? rule : oldValue.and(rule));
        return this;
    }

    public SidedInventoryBuilder addExtractionRule(SlotDescription slot, Rule rule) {
        extractionRules.compute(slot.getIndex(), (key, oldValue) -> oldValue == null ? rule : oldValue.and(rule));
        return this;
    }

    @Override
    public SidedInventoryBuilder addOnDirty(Runnable onDirty) {
        return (SidedInventoryBuilder) super.addOnDirty(onDirty);
    }

    @Override
    public ImplementedSidedInventory build() {
        if (slots.isEmpty()) {
            return ImplementedSidedInventory.EMPTY;
        }
        return new ImplementedSidedInventory() {
            private final DefaultedList<ItemStack> items = DefaultedList.ofSize(slots.size(), ItemStack.EMPTY);

            @Override
            public DefaultedList<ItemStack> getItems() {
                return items;
            }

            @Override
            public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
                return isValid(slot, stack) && insertionRules.get(slot).test(dir, stack);
            }

            @Override
            public boolean canExtract(int slot, ItemStack stack, Direction side) {
                return extractionRules.get(slot).test(side, stack);
            }

            @Override
            public boolean isValid(int slot, ItemStack stack) {
                return slots.get(slot).canInsert(stack);
            }

            @Override
            public void markDirty() {
                onDirty.forEach(Runnable::run);
            }
        };
    }

    @FunctionalInterface
    public interface Rule {
        boolean test(@Nullable Direction dir, ItemStack stack);

        default Rule and(Rule other) {
            Objects.requireNonNull(other);
            return (dir, stack) -> test(dir, stack) && other.test(dir, stack);
        }
    }
}
package dev.falseresync.vivatech.api.inventory.item;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ItemInventoryBuilder {
    protected final Int2ObjectMap<ItemSlotDescription> slots = new Int2ObjectRBTreeMap<>();
    protected final Int2ObjectMap<Rule> insertionRules = new Int2ObjectRBTreeMap<>();
    protected final Int2ObjectMap<Rule> extractionRules = new Int2ObjectRBTreeMap<>();
    protected final ObjectSet<Runnable> onDirty = new ObjectArraySet<>();

    public void addSlot(ItemSlotDescription slot) {
        Preconditions.checkArgument(!slots.containsKey(slot.getIndex()), "Slot with index %d already exists", slot.getIndex());
        slots.put(slot.getIndex(), slot);
        insertionRules.computeIfAbsent(slot.getIndex(), key -> (dir, stack) -> true);
        extractionRules.computeIfAbsent(slot.getIndex(), key -> (dir, stack) -> true);
    }

    public void addInsertionRule(ItemSlotDescription slot, Rule rule) {
        insertionRules.compute(slot.getIndex(), (key, oldValue) -> oldValue == null ? rule : oldValue.and(rule));
    }

    public void addExtractionRule(ItemSlotDescription slot, Rule rule) {
        extractionRules.compute(slot.getIndex(), (key, oldValue) -> oldValue == null ? rule : oldValue.and(rule));
    }

    public void addOnDirty(Runnable onDirty) {
        this.onDirty.add(onDirty);
    }

    @FunctionalInterface
    public interface Rule {
        boolean test(@Nullable Direction dir, ItemStack stack);

        default Rule and(Rule other) {
            Objects.requireNonNull(other);
            return (dir, stack) -> test(dir, stack) && other.test(dir, stack);
        }
    }

    public ImplementedItemInventory build() {
        if (slots.isEmpty()) {
            return ImplementedItemInventory.EMPTY;
        }
        return new ImplementedItemInventory() {
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
}

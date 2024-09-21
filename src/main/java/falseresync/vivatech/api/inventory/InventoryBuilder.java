package falseresync.vivatech.api.inventory;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import net.minecraft.inventory.Inventory;

public abstract class InventoryBuilder<I extends Inventory> {
    protected final Int2ObjectMap<SlotDescription> slots = new Int2ObjectRBTreeMap<>();
    protected final ObjectSet<Runnable> onDirty = new ObjectArraySet<>();

    public InventoryBuilder<I> addSlot(SlotDescription slot) {
        Preconditions.checkArgument(!slots.containsKey(slot.getIndex()), "Slot with index %d already exists", slot.getIndex());
        slots.put(slot.getIndex(), slot);
        return this;
    }

    public InventoryBuilder<I> addSlots(SlotDescription... slots) {
        for (SlotDescription slot : slots) {
            addSlot(slot);
        }
        return this;
    }

    public InventoryBuilder<I> addOnDirty(Runnable onDirty) {
        this.onDirty.add(onDirty);
        return this;
    }

    public abstract I build();
}

package falseresync.vivatech.common.data;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

public record InventoryComponent(ImmutableList<ItemStack> stacks, int size) implements TooltipData {
    public static final Codec<InventoryComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Slot.CODEC.listOf().xmap(InventoryComponent::fromSlots, InventoryComponent::toSlots)
                    .fieldOf("slots").forGetter(InventoryComponent::stacks),
            Codec.INT.fieldOf("size").forGetter(InventoryComponent::size)
    ).apply(instance, InventoryComponent::createRespectingSize));
    public static final PacketCodec<RegistryByteBuf, InventoryComponent> PACKET_CODEC = PacketCodec.tuple(
            ItemStack.OPTIONAL_LIST_PACKET_CODEC, InventoryComponent::stacks,
            PacketCodecs.INTEGER, InventoryComponent::size,
            InventoryComponent::new
    );

    public InventoryComponent(List<ItemStack> stacks, int size) {
        this(ImmutableList.copyOf(stacks), size);
    }

    private static InventoryComponent createRespectingSize(List<ItemStack> stacks, int size) {
        if (size > stacks.size()) {
            var builder = ImmutableList.<ItemStack>builder();
            builder.addAll(stacks);
            for (int i = stacks.size(); i < size; i++) {
                builder.add(ItemStack.EMPTY);
            }

            return new InventoryComponent(builder.build(), size);
        }

        return new InventoryComponent(stacks, size);
    }

    public static InventoryComponent createDefault(int size) {
        return new InventoryComponent(DefaultedList.ofSize(size, ItemStack.EMPTY), size);
    }

    public static List<ItemStack> fromSlots(List<Slot> slots) {
        var stacks = DefaultedList.ofSize(slots.stream().mapToInt(Slot::index).max().orElseThrow() + 1, ItemStack.EMPTY);
        for (Slot slot : slots) {
            stacks.set(slot.index, slot.stack);
        }
        return stacks;
    }

    public static List<Slot> toSlots(List<ItemStack> stacks) {
        var builder = ImmutableList.<Slot>builder();
        for (int i = 0; i < stacks.size(); i++) {
            var stack = stacks.get(i);
            if (!stack.isEmpty()) {
                builder.add(new Slot(i, stack));
            }
        }
        return builder.build();
    }

    public int getSlotWithStack(ItemStack stack) {
        for (int i = 0; i < size(); i++) {
            if (!stacks.get(i).isEmpty() && ItemStack.areItemsAndComponentsEqual(stack, stacks.get(i))) {
                return i;
            }
        }

        return -1;
    }

    public EphemeralInventory toModifiable() {
        return new EphemeralInventory(this);
    }

    @Override
    public String toString() {
        return "InventoryComponent" + stacks;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof InventoryComponent that) {
            return this.stacks.size() == that.stacks.size() && ItemStack.stacksEqual(this.stacks, that.stacks);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return ItemStack.listHashCode(stacks);
    }

    public record Slot(int index, ItemStack stack) {
        public static final Codec<Slot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.intRange(0, 255).fieldOf("index").forGetter(Slot::index),
                ItemStack.CODEC.fieldOf("stack").forGetter(Slot::stack)
        ).apply(instance, Slot::new));
    }
}

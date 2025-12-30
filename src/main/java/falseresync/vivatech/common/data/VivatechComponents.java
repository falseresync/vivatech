package falseresync.vivatech.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import falseresync.lib.registry.RegistryObject;
import falseresync.vivatech.common.data.InventoryComponent;
import falseresync.vivatech.common.data.ItemBarComponent;
import falseresync.vivatech.common.item.focus.FocusItem;
import falseresync.vivatech.common.item.focus.FocusPlating;
import java.util.UUID;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

public class VivatechComponents {
    // Generic
    public static final @RegistryObject DataComponentType<GlobalPos> CONNECTION =
            DataComponentType.<GlobalPos>builder().persistent(GlobalPos.CODEC).networkSynchronized(GlobalPos.STREAM_CODEC).build();
    public static final @RegistryObject DataComponentType<falseresync.vivatech.common.data.ItemBarComponent> ITEM_BAR =
            DataComponentType.<falseresync.vivatech.common.data.ItemBarComponent>builder().persistent(falseresync.vivatech.common.data.ItemBarComponent.CODEC).networkSynchronized(ItemBarComponent.PACKET_CODEC).build();
    public static final @RegistryObject DataComponentType<Boolean> IN_USE =
            DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build();
    public static final @RegistryObject DataComponentType<falseresync.vivatech.common.data.InventoryComponent> INVENTORY =
            DataComponentType.<falseresync.vivatech.common.data.InventoryComponent>builder().persistent(falseresync.vivatech.common.data.InventoryComponent.CODEC).networkSynchronized(InventoryComponent.PACKET_CODEC).build();
    public static final @RegistryObject DataComponentType<Integer> INVENTORY_SIZE =
            DataComponentType.<Integer>builder().persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.INT).build();
    public static final @RegistryObject DataComponentType<UUID> UUID =
            DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).build();

    // Gadget
    public static final @RegistryObject DataComponentType<ItemStack> EQUIPPED_FOCUS_ITEM =
            DataComponentType.<ItemStack>builder()
                    .persistent(ItemStack.CODEC.validate(stack -> stack.getItem() instanceof FocusItem
                            ? DataResult.success(stack)
                            : DataResult.error(() -> "Only FocusItems are allowed")))
                    .networkSynchronized(ItemStack.STREAM_CODEC)
                    .build();
    public static final @RegistryObject DataComponentType<Integer> CHARGE =
            DataComponentType.<Integer>builder().persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.INT).build();
    public static final @RegistryObject DataComponentType<Integer> MAX_CHARGE =
            DataComponentType.<Integer>builder().persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.INT).build();
    public static final @RegistryObject DataComponentType<Integer> CHARGE_DEFICIT =
            DataComponentType.<Integer>builder().persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.INT).build();
    public static final @RegistryObject DataComponentType<Boolean> INFINITE_CHARGE =
            DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build();

    // Focuses
    public static final @RegistryObject DataComponentType<Integer> FOCUS_PLATING =
            DataComponentType.<Integer>builder().persistent(ExtraCodecs.intRange(0, FocusPlating.values().length - 1)).networkSynchronized(ByteBufCodecs.INT).build();
    public static final @RegistryObject DataComponentType<GlobalPos> WARP_FOCUS_ANCHOR =
            DataComponentType.<GlobalPos>builder().persistent(GlobalPos.CODEC).networkSynchronized(GlobalPos.STREAM_CODEC).build();
    public static final @RegistryObject DataComponentType<GlobalPos> WARP_FOCUS_PERSISTENT_ANCHOR =
            DataComponentType.<GlobalPos>builder().persistent(GlobalPos.CODEC).networkSynchronized(GlobalPos.STREAM_CODEC).build();
    public static final @RegistryObject DataComponentType<Boolean> WARP_FOCUS_BLOCK_ONLY_MODE =
            DataComponentType.<Boolean>builder().persistent(Codec.BOOL).networkSynchronized(ByteBufCodecs.BOOL).build();
    public static final @RegistryObject DataComponentType<UUID> ENERGY_VEIL_UUID =
            DataComponentType.<UUID>builder().persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC).build();
}

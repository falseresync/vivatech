package falseresync.vivatech.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import falseresync.lib.registry.RegistryObject;
import falseresync.vivatech.common.item.focus.FocusItem;
import falseresync.vivatech.common.item.focus.FocusPlating;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.Uuids;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.GlobalPos;

import java.util.UUID;

public class VivatechComponents {
    // Generic
    public static final @RegistryObject ComponentType<GlobalPos> CONNECTION =
            ComponentType.<GlobalPos>builder().codec(GlobalPos.CODEC).packetCodec(GlobalPos.PACKET_CODEC).build();
    public static final @RegistryObject ComponentType<ItemBarComponent> ITEM_BAR =
            ComponentType.<ItemBarComponent>builder().codec(ItemBarComponent.CODEC).packetCodec(ItemBarComponent.PACKET_CODEC).build();
    public static final @RegistryObject ComponentType<Boolean> IN_USE =
            ComponentType.<Boolean>builder().codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL).build();
    public static final @RegistryObject ComponentType<Boolean> TOOLTIP_OVERRIDDEN =
            ComponentType.<Boolean>builder().codec(Codec.BOOL).packetCodec(PacketCodecs.BOOL).build();
    public static final @RegistryObject ComponentType<InventoryComponent> INVENTORY =
            ComponentType.<InventoryComponent>builder().codec(InventoryComponent.CODEC).packetCodec(InventoryComponent.PACKET_CODEC).build();
    public static final @RegistryObject ComponentType<Integer> INVENTORY_SIZE =
            ComponentType.<Integer>builder().codec(Codecs.POSITIVE_INT).packetCodec(PacketCodecs.INTEGER).build();
    public static final @RegistryObject ComponentType<UUID> UUID =
            ComponentType.<UUID>builder().codec(Uuids.INT_STREAM_CODEC).packetCodec(Uuids.PACKET_CODEC).build();

    // Gadget
    public static final @RegistryObject ComponentType<ItemStack> EQUIPPED_FOCUS_ITEM =
            ComponentType.<ItemStack>builder()
                    .codec(ItemStack.CODEC.validate(stack -> stack.getItem() instanceof FocusItem
                            ? DataResult.success(stack)
                            : DataResult.error(() -> "Only FocusItems are allowed")))
                    .packetCodec(ItemStack.PACKET_CODEC)
                    .build();
    public static final @RegistryObject ComponentType<Integer> CHARGE =
            ComponentType.<Integer>builder().codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.INTEGER).build();
    public static final @RegistryObject ComponentType<Integer> MAX_CHARGE =
            ComponentType.<Integer>builder().codec(Codecs.POSITIVE_INT).packetCodec(PacketCodecs.INTEGER).build();
    public static final @RegistryObject ComponentType<Integer> CHARGE_DEFICIT =
            ComponentType.<Integer>builder().codec(Codecs.POSITIVE_INT).packetCodec(PacketCodecs.INTEGER).build();

    // Focuses
    public static final @RegistryObject ComponentType<Integer> FOCUS_PLATING =
            ComponentType.<Integer>builder().codec(Codecs.rangedInt(0, FocusPlating.values().length - 1)).packetCodec(PacketCodecs.INTEGER).build();
    public static final @RegistryObject ComponentType<GlobalPos> WARP_FOCUS_ANCHOR =
            ComponentType.<GlobalPos>builder().codec(GlobalPos.CODEC).packetCodec(GlobalPos.PACKET_CODEC).build();
    public static final @RegistryObject ComponentType<UUID> ENERGY_VEIL_UUID =
            ComponentType.<UUID>builder().codec(Uuids.INT_STREAM_CODEC).packetCodec(Uuids.PACKET_CODEC).build();
}

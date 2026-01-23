package falseresync.vivatech.world.electricity.wire;

import falseresync.vivatech.Vivatech;
import falseresync.vivatech.world.item.VivatechItems;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Supplier;

public record WireType(Supplier<Item> item, int voltage, int maxCurrent, int overcurrentToleranceTime) {
    public static final ResourceKey<Registry<WireType>> REGISTRY_KEY = ResourceKey.createRegistryKey(Vivatech.id("wire_types"));
    public static final Registry<WireType> REGISTRY =
            FabricRegistryBuilder.create(REGISTRY_KEY)
                    .attribute(RegistryAttribute.MODDED)
                    .attribute(RegistryAttribute.SYNCED)
                    .buildAndRegister();
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<WireType>> PACKET_CODEC = ByteBufCodecs.holderRegistry(REGISTRY_KEY);

    public static final WireType V_230 = register("v_230", new WireType(() -> VivatechItems.COPPER_WIRE, 230, 32, 100));

    public static void init() {
    }

    private static WireType register(String name, WireType wireType) {
        return Registry.register(REGISTRY, Vivatech.id(name), wireType);
    }
}

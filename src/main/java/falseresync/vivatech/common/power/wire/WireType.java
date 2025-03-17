package falseresync.vivatech.common.power.wire;

import falseresync.lib.registry.RegistryObject;
import falseresync.vivatech.common.item.VivatechItems;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.item.Item;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;

import static falseresync.vivatech.common.Vivatech.vtId;

public record WireType(Item item, int voltage, int maxCurrent, int overcurrentToleranceTime) {
    public static final Registry<WireType> REGISTRY =
            FabricRegistryBuilder.<WireType>createSimple(RegistryKey.ofRegistry(vtId("wire_types")))
                    .attribute(RegistryAttribute.MODDED)
                    .attribute(RegistryAttribute.SYNCED)
                    .buildAndRegister();
    public static final PacketCodec<RegistryByteBuf, RegistryEntry<WireType>> PACKET_CODEC = PacketCodecs.registryEntry(REGISTRY.getKey());

    public static final @RegistryObject WireType V_230 = new WireType(VivatechItems.WIRE, 230, 32, 100);
}

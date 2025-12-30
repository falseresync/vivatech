package falseresync.vivatech.common.power.wire;

import falseresync.lib.registry.RegistryObject;
import falseresync.vivatech.common.item.VivatechItems;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import static falseresync.vivatech.common.Vivatech.vtId;

public record WireType(Item item, int voltage, int maxCurrent, int overcurrentToleranceTime) {
    public static final Registry<WireType> REGISTRY =
            FabricRegistryBuilder.<WireType>createSimple(ResourceKey.createRegistryKey(vtId("wire_types")))
                    .attribute(RegistryAttribute.MODDED)
                    .attribute(RegistryAttribute.SYNCED)
                    .buildAndRegister();
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<WireType>> PACKET_CODEC = ByteBufCodecs.holderRegistry(REGISTRY.key());

    public static final @RegistryObject WireType V_230 = new WireType(VivatechItems.WIRE, 230, 32, 100);
}

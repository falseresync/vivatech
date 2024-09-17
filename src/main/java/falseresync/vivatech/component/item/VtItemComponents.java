package falseresync.vivatech.component.item;

import falseresync.vivatech.api.registry.RegistryObject;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;

public final class VtItemComponents {
    public static final @RegistryObject ComponentType<Integer> MAX_ACCUMULATED_LIFE =
            ComponentType.<Integer>builder().codec(Codecs.POSITIVE_INT).packetCodec(PacketCodecs.VAR_INT).build();
    public static final @RegistryObject ComponentType<Integer> ACCUMULATED_LIFE =
            ComponentType.<Integer>builder().codec(Codecs.NONNEGATIVE_INT).packetCodec(PacketCodecs.VAR_INT).build();
}

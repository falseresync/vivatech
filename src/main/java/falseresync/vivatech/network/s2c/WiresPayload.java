package falseresync.vivatech.network.s2c;

import falseresync.vivatech.common.power.Wire;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.Set;

import static falseresync.vivatech.common.Vivatech.vtId;

public record WiresPayload(Set<Wire> wires) implements CustomPayload {
    public static final CustomPayload.Id<WiresPayload> ID = new Id<>(vtId("wires"));
    public static final PacketCodec<RegistryByteBuf, WiresPayload> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.collection(ObjectOpenHashSet::new, Wire.PACKET_CODEC), WiresPayload::wires,
            WiresPayload::new
    );

    @Override
    public Id<WiresPayload> getId() {
        return ID;
    }
}

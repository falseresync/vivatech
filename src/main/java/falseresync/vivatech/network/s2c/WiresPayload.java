package falseresync.vivatech.network.s2c;

import falseresync.vivatech.common.power.Wire;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.Set;

import static falseresync.vivatech.common.Vivatech.vtId;

public class WiresPayload {
    public record Added(Set<Wire> wires) implements CustomPayload {
        public static final CustomPayload.Id<WiresPayload.Added> ID = new Id<>(vtId("wires_added"));
        public static final PacketCodec<RegistryByteBuf, WiresPayload.Added> PACKET_CODEC = PacketCodec.tuple(
                PacketCodecs.collection(ObjectOpenHashSet::new, Wire.PACKET_CODEC), WiresPayload.Added::wires,
                WiresPayload.Added::new
        );

        @Override
        public Id<WiresPayload.Added> getId() {
            return ID;
        }
    }

    public record Removed(Set<Wire> wires) implements CustomPayload {
        public static final CustomPayload.Id<WiresPayload.Removed> ID = new Id<>(vtId("wires_removed"));
        public static final PacketCodec<RegistryByteBuf, WiresPayload.Removed> PACKET_CODEC = PacketCodec.tuple(
                PacketCodecs.collection(ObjectOpenHashSet::new, Wire.PACKET_CODEC), WiresPayload.Removed::wires,
                WiresPayload.Removed::new
        );

        @Override
        public Id<WiresPayload.Removed> getId() {
            return ID;
        }
    }
}

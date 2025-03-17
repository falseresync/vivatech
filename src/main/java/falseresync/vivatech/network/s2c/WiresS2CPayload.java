package falseresync.vivatech.network.s2c;

import falseresync.vivatech.common.power.wire.Wire;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import java.util.Set;

import static falseresync.vivatech.common.Vivatech.vtId;

public class WiresS2CPayload {
    public record Added(Set<Wire> wires) implements CustomPayload {
        public static final CustomPayload.Id<WiresS2CPayload.Added> ID = new Id<>(vtId("wires_added"));
        public static final PacketCodec<RegistryByteBuf, WiresS2CPayload.Added> PACKET_CODEC = PacketCodec.tuple(
                PacketCodecs.collection(ObjectOpenHashSet::new, Wire.PACKET_CODEC), WiresS2CPayload.Added::wires,
                WiresS2CPayload.Added::new
        );

        @Override
        public Id<WiresS2CPayload.Added> getId() {
            return ID;
        }
    }

    public record Removed(Set<Wire> wires) implements CustomPayload {
        public static final CustomPayload.Id<WiresS2CPayload.Removed> ID = new Id<>(vtId("wires_removed"));
        public static final PacketCodec<RegistryByteBuf, WiresS2CPayload.Removed> PACKET_CODEC = PacketCodec.tuple(
                PacketCodecs.collection(ObjectOpenHashSet::new, Wire.PACKET_CODEC), WiresS2CPayload.Removed::wires,
                WiresS2CPayload.Removed::new
        );

        @Override
        public Id<WiresS2CPayload.Removed> getId() {
            return ID;
        }
    }
}

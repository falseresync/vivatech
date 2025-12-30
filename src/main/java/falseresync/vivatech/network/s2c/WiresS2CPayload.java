package falseresync.vivatech.network.s2c;

import falseresync.vivatech.common.power.wire.Wire;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import static falseresync.vivatech.common.Vivatech.vtId;

public class WiresS2CPayload {
    public record Added(Set<Wire> wires) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<WiresS2CPayload.Added> ID = new Type<>(vtId("wires_added"));
        public static final StreamCodec<RegistryFriendlyByteBuf, WiresS2CPayload.Added> PACKET_CODEC = StreamCodec.composite(
                ByteBufCodecs.collection(ObjectOpenHashSet::new, Wire.PACKET_CODEC), WiresS2CPayload.Added::wires,
                WiresS2CPayload.Added::new
        );

        @Override
        public Type<WiresS2CPayload.Added> type() {
            return ID;
        }
    }

    public record Removed(Set<Wire> wires) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<WiresS2CPayload.Removed> ID = new Type<>(vtId("wires_removed"));
        public static final StreamCodec<RegistryFriendlyByteBuf, WiresS2CPayload.Removed> PACKET_CODEC = StreamCodec.composite(
                ByteBufCodecs.collection(ObjectOpenHashSet::new, Wire.PACKET_CODEC), WiresS2CPayload.Removed::wires,
                WiresS2CPayload.Removed::new
        );

        @Override
        public Type<WiresS2CPayload.Removed> type() {
            return ID;
        }
    }
}

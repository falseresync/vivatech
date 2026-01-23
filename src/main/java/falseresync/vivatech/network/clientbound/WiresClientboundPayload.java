package falseresync.vivatech.network.clientbound;

import falseresync.vivatech.Vivatech;
import falseresync.vivatech.world.electricity.wire.Wire;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Set;


public class WiresClientboundPayload {
    public record Added(Set<Wire> wires) implements CustomPacketPayload {
        public static final Type<Added> ID = new Type<>(Vivatech.id("wires_added"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Added> PACKET_CODEC = StreamCodec.composite(
                ByteBufCodecs.collection(ObjectOpenHashSet::new, Wire.PACKET_CODEC), Added::wires,
                Added::new
        );

        @Override
        public Type<Added> type() {
            return ID;
        }
    }

    public record Removed(Set<Wire> wires) implements CustomPacketPayload {
        public static final Type<Removed> ID = new Type<>(Vivatech.id("wires_removed"));
        public static final StreamCodec<RegistryFriendlyByteBuf, Removed> PACKET_CODEC = StreamCodec.composite(
                ByteBufCodecs.collection(ObjectOpenHashSet::new, Wire.PACKET_CODEC), Removed::wires,
                Removed::new
        );

        @Override
        public Type<Removed> type() {
            return ID;
        }
    }
}

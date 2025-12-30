package falseresync.vivatech.network.c2s;

import static falseresync.vivatech.common.Vivatech.vtId;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ChangeFocusC2SPayload(falseresync.vivatech.network.c2s.FocusDestination destination, int slot) implements CustomPacketPayload {
    public static final Type<ChangeFocusC2SPayload> ID = new Type<>(vtId("change_gadget_focus"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ChangeFocusC2SPayload> PACKET_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT.map(it -> falseresync.vivatech.network.c2s.FocusDestination.values[it], FocusDestination::ordinal).cast(), ChangeFocusC2SPayload::destination,
                    ByteBufCodecs.INT, ChangeFocusC2SPayload::slot,
                    ChangeFocusC2SPayload::new
            );

    @Override
    public Type<ChangeFocusC2SPayload> type() {
        return ID;
    }
}

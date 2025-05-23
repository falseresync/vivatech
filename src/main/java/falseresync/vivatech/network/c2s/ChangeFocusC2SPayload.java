package falseresync.vivatech.network.c2s;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

import static falseresync.vivatech.common.Vivatech.vtId;

public record ChangeFocusC2SPayload(FocusDestination destination, int slot) implements CustomPayload {
    public static final Id<ChangeFocusC2SPayload> ID = new Id<>(vtId("change_gadget_focus"));
    public static final PacketCodec<RegistryByteBuf, ChangeFocusC2SPayload> PACKET_CODEC =
            PacketCodec.tuple(
                    PacketCodecs.INTEGER.xmap(it -> FocusDestination.values[it], FocusDestination::ordinal).cast(), ChangeFocusC2SPayload::destination,
                    PacketCodecs.INTEGER, ChangeFocusC2SPayload::slot,
                    ChangeFocusC2SPayload::new
            );

    @Override
    public Id<ChangeFocusC2SPayload> getId() {
        return ID;
    }
}

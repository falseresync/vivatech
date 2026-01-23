package falseresync.vivatech.network.serverbound;

import falseresync.vivatech.Vivatech;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.ChunkPos;

import java.util.List;
import java.util.stream.Collectors;

public record RequestWiresServerboundPayload(List<ChunkPos> chunks) implements CustomPacketPayload {
    public static final Type<RequestWiresServerboundPayload> ID = new Type<>(Vivatech.id("request_wires_chunks"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestWiresServerboundPayload> PACKET_CODEC =
            ByteBufCodecs.collection(n -> (LongList) new LongArrayList(n), ByteBufCodecs.VAR_LONG)
                    .map(
                            it -> new RequestWiresServerboundPayload(it.longStream().mapToObj(ChunkPos::unpack).toList()),
                            it -> it.chunks.stream().map(ChunkPos::pack).collect(Collectors.toCollection(LongArrayList::new)))
                    .cast();

    @Override
    public Type<RequestWiresServerboundPayload> type() {
        return ID;
    }
}

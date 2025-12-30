package falseresync.vivatech.network.c2s;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.ChunkPos;

import static falseresync.vivatech.common.Vivatech.vtId;

public record RequestWiresChunksC2SPayload(List<ChunkPos> chunks) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<RequestWiresChunksC2SPayload> ID = new Type<>(vtId("request_wires_chunks"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestWiresChunksC2SPayload> PACKET_CODEC =
            ByteBufCodecs.collection(n -> (LongList) new LongArrayList(n), ByteBufCodecs.VAR_LONG)
                    .map(
                            it -> new RequestWiresChunksC2SPayload(it.longStream().mapToObj(ChunkPos::new).toList()),
                            it -> it.chunks.stream().map(ChunkPos::toLong).collect(Collectors.toCollection(LongArrayList::new)))
                    .cast();

    @Override
    public Type<RequestWiresChunksC2SPayload> type() {
        return ID;
    }
}

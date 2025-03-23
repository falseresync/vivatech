package falseresync.vivatech.network.c2s;

import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.math.ChunkPos;

import java.util.List;
import java.util.stream.Collectors;

import static falseresync.vivatech.common.Vivatech.vtId;

public record RequestWiresChunksC2SPayload(List<ChunkPos> chunks) implements CustomPayload {
    public static final CustomPayload.Id<RequestWiresChunksC2SPayload> ID = new Id<>(vtId("request_wires_chunks"));
    public static final PacketCodec<RegistryByteBuf, RequestWiresChunksC2SPayload> PACKET_CODEC =
            PacketCodecs.collection(n -> (LongList) new LongArrayList(n), PacketCodecs.VAR_LONG)
                    .xmap(
                            it -> new RequestWiresChunksC2SPayload(it.longStream().mapToObj(ChunkPos::new).toList()),
                            it -> it.chunks.stream().map(ChunkPos::toLong).collect(Collectors.toCollection(LongArrayList::new)))
                    .cast();

    @Override
    public Id<RequestWiresChunksC2SPayload> getId() {
        return ID;
    }
}

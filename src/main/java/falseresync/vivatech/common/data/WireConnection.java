package falseresync.vivatech.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;

public record WireConnection(BlockPos from, BlockPos to) {
    public static final Codec<WireConnection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("from").forGetter(WireConnection::from),
            BlockPos.CODEC.fieldOf("to").forGetter(WireConnection::to)
    ).apply(instance, WireConnection::new));
    public static final PacketCodec<RegistryByteBuf, WireConnection> PACKET_CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, WireConnection::from,
            BlockPos.PACKET_CODEC, WireConnection::to,
            WireConnection::new
    );
}

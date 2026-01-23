package falseresync.vivatech.world.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record WireConnection(BlockPos from, BlockPos to) {
    public static final Codec<WireConnection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BlockPos.CODEC.fieldOf("from").forGetter(WireConnection::from),
            BlockPos.CODEC.fieldOf("to").forGetter(WireConnection::to)
    ).apply(instance, WireConnection::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, WireConnection> PACKET_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, WireConnection::from,
            BlockPos.STREAM_CODEC, WireConnection::to,
            WireConnection::new
    );
}

package falseresync.vivatech.common.power;

import com.google.common.collect.ImmutableSet;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;

public record Wire(ImmutableSet<BlockPos> positions, BlockPos u, BlockPos v, Vec3d middle, ChunkPos chunkPos) {
    public static final PacketCodec<RegistryByteBuf, Wire> PACKET_CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, Wire::u,
            BlockPos.PACKET_CODEC, Wire::v,
            Wire::createClientWire
    );

    @SuppressWarnings("JavaExistingMethodCanBeUsed")
    public static Wire createServerWire(BlockPos u, BlockPos v) {
        var middle = u.add(v).toCenterPos().multiply(0.5f);
        var chunkPos = new ChunkPos(ChunkSectionPos.getSectionCoordFloored(middle.x), ChunkSectionPos.getSectionCoordFloored(middle.z));
        return new Wire(ImmutableSet.of(u, v), u, v, middle, chunkPos);
    }

    public static Wire createClientWire(BlockPos u, BlockPos v) {
        var middle = u.add(v).toCenterPos().multiply(0.5f);
        var chunkPos = new ChunkPos(ChunkSectionPos.getSectionCoordFloored(middle.x), ChunkSectionPos.getSectionCoordFloored(middle.z));
        return new Wire(ImmutableSet.of(u, v), u, v, middle, chunkPos);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Wire wire)) return false;
        return Objects.equals(positions, wire.positions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(positions);
    }
}

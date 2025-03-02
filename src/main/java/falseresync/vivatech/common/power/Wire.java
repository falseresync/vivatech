package falseresync.vivatech.common.power;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Objects;
import java.util.Set;

public record Wire(Set<BlockPos> positions, BlockPos from, BlockPos to, Vec3d middle, ChunkPos chunkPos, double squaredLength, double length, boolean removed) {
    public static final Codec<Wire> CODEC =
            Codec.list(BlockPos.CODEC, 2, 2).xmap(it -> new Wire(it.getFirst(), it.getLast()), it -> List.of(it.from, it.to));
    public static final PacketCodec<RegistryByteBuf, Wire> PACKET_CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, Wire::from,
            BlockPos.PACKET_CODEC, Wire::to,
            PacketCodecs.BOOL, Wire::removed,
            Wire::new
    );

    private Wire(BlockPos from, BlockPos to, Vec3d middle, double squaredLength, boolean removed) {
        this(
                Set.of(from, to), from, to, middle,
                new ChunkPos(ChunkSectionPos.getSectionCoordFloored(middle.x), ChunkSectionPos.getSectionCoordFloored(middle.z)),
                squaredLength, Math.sqrt(squaredLength), removed);
    }

    private Wire(BlockPos from, BlockPos to, boolean removed) {
        this(from, to, from.add(to).toCenterPos().multiply(0.5f), to.getSquaredDistance(from), removed);
    }

    public Wire(BlockPos from, BlockPos to) {
        this(from, to, false);
    }

    public Wire withRemoved(boolean removed) {
        return new Wire(positions, from, to, middle, chunkPos, squaredLength, length, removed);
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

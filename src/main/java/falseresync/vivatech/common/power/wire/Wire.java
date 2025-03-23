package falseresync.vivatech.common.power.wire;

import com.google.common.collect.ImmutableSet;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.joml.Vector3f;

import java.util.Objects;

public record Wire(
        WireType type,
        ImmutableSet<BlockPos> positions,
        BlockPos u,
        BlockPos v,
        Vector3f start,
        Vector3f end,
        Vector3f middle,
        float length,
        ChunkPos chunkPos,
        float loadCoefficient
) {
    public static final PacketCodec<RegistryByteBuf, Wire> PACKET_CODEC = PacketCodec.tuple(
            WireType.PACKET_CODEC.xmap(RegistryEntry::value, WireType.REGISTRY::getEntry), Wire::type,
            BlockPos.PACKET_CODEC, Wire::u,
            BlockPos.PACKET_CODEC, Wire::v,
            PacketCodecs.FLOAT, Wire::loadCoefficient,
            Wire::create
    );

    public static Wire create(WireType type, BlockPos u, BlockPos v, float loadCoefficient) {
        var start = u.toCenterPos().toVector3f();
        var end = v.toCenterPos().toVector3f();
        var middle = start.add(end, new Vector3f()).mul(0.5f);
        return new Wire(type, ImmutableSet.of(u, v), u, v, start, end, middle, start.distance(end),
                new ChunkPos(ChunkSectionPos.getSectionCoordFloored(middle.x), ChunkSectionPos.getSectionCoordFloored(middle.z)),
                loadCoefficient);
    }

    public void drop(World world, WireType type, DropRule dropRule) {
        switch (dropRule) {
            case NO_DROP -> {
            }
            case PARTIAL ->
                    ItemScatterer.spawn(world, middle.x, middle.y, middle.z, new ItemStack(type.item(), MathHelper.floor(length * world.random.nextFloat())));
            case FULL ->
                    ItemScatterer.spawn(world, middle.x, middle.y, middle.z, new ItemStack(type.item(), MathHelper.floor(length)));
        }
    }

    public static int getItemCount(BlockPos u, BlockPos v) {
        return MathHelper.ceil(Math.sqrt(u.getSquaredDistance(v)));
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

    public enum DropRule {
        NO_DROP,
        PARTIAL,
        FULL
    }
}

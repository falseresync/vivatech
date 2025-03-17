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
        ChunkPos chunkPos
) {
    public enum DropRule {
        NO_DROP,
        PARTIAL,
        FULL
    }

    public static final PacketCodec<RegistryByteBuf, Wire> PACKET_CODEC = PacketCodec.tuple(
            WireType.PACKET_CODEC.xmap(RegistryEntry::value, WireType.REGISTRY::getEntry), Wire::type,
            BlockPos.PACKET_CODEC, Wire::u,
            BlockPos.PACKET_CODEC, Wire::v,
            Wire::createClientWire
    );

    public static Wire createServerWire(WireType type, BlockPos u, BlockPos v) {
        var middle = toCenterPos(u.add(v)).mul(0.5f);
        var chunkPos = new ChunkPos(ChunkSectionPos.getSectionCoordFloored(middle.x), ChunkSectionPos.getSectionCoordFloored(middle.z));
        return new Wire(type, ImmutableSet.of(u, v), u, v, null, null, middle, chunkPos);
    }

    public static Wire createClientWire(WireType type, BlockPos u, BlockPos v) {
        var middle = toCenterPos(u.add(v)).mul(0.5f);
        var chunkPos = new ChunkPos(ChunkSectionPos.getSectionCoordFloored(middle.x), ChunkSectionPos.getSectionCoordFloored(middle.z));
        return new Wire(type, ImmutableSet.of(u, v), u, v, toCenterPos(u), toCenterPos(v), middle, chunkPos);
    }

    private static Vector3f toCenterPos(BlockPos pos) {
        return new Vector3f(pos.getX() + 0.5f, pos.getY() + 0.5f, pos.getZ() + 0.5f);
    }

    public void drop(World world, WireType type, DropRule dropRule) {
        switch (dropRule) {
            case NO_DROP -> {}
            case PARTIAL ->
                    ItemScatterer.spawn(world, middle.x, middle.y, middle.z, new ItemStack(type.item(), MathHelper.floor(u.subtract(v).toCenterPos().length() * world.random.nextFloat())));
            case FULL ->
                    ItemScatterer.spawn(world, middle.x, middle.y, middle.z, new ItemStack(type.item(), MathHelper.floor(u.subtract(v).toCenterPos().length())));
        }
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

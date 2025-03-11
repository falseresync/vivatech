package falseresync.vivatech.common.power;

import com.google.common.collect.ImmutableSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.Objects;

public record Wire(
        ImmutableSet<BlockPos> positions,
        BlockPos u,
        BlockPos v,
        @Environment(EnvType.CLIENT) Vec3d start,
        @Environment(EnvType.CLIENT) Vec3d end,
        Vec3d middle,
        ChunkPos chunkPos
) {
    public static final PacketCodec<RegistryByteBuf, Wire> PACKET_CODEC = PacketCodec.tuple(
            BlockPos.PACKET_CODEC, Wire::u,
            BlockPos.PACKET_CODEC, Wire::v,
            Wire::createClientWire
    );

    public static Wire createServerWire(BlockPos u, BlockPos v) {
        var middle = u.add(v).toCenterPos().multiply(0.5f);
        var chunkPos = new ChunkPos(ChunkSectionPos.getSectionCoordFloored(middle.x), ChunkSectionPos.getSectionCoordFloored(middle.z));
        return new Wire(ImmutableSet.of(u, v), u, v, null, null, middle, chunkPos);
    }

    public static Wire createClientWire(BlockPos u, BlockPos v) {
        var middle = u.add(v).toCenterPos().multiply(0.5f);
        var chunkPos = new ChunkPos(ChunkSectionPos.getSectionCoordFloored(middle.x), ChunkSectionPos.getSectionCoordFloored(middle.z));
        return new Wire(ImmutableSet.of(u, v), u, v, u.toCenterPos(), v.toCenterPos(), middle, chunkPos);
    }

    public void drop(World world, WireType type) {
        ItemScatterer.spawn(world, middle.x, middle.y, middle.z, new ItemStack(type.item(), MathHelper.floor(u.subtract(v).toCenterPos().length())));
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

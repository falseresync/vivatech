package falseresync.vivatech.world.electricity.wire;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
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
    public static final StreamCodec<RegistryFriendlyByteBuf, Wire> PACKET_CODEC = StreamCodec.composite(
            WireType.PACKET_CODEC.map(Holder::value, WireType.REGISTRY::wrapAsHolder), Wire::type,
            BlockPos.STREAM_CODEC, Wire::u,
            BlockPos.STREAM_CODEC, Wire::v,
            ByteBufCodecs.FLOAT, Wire::loadCoefficient,
            Wire::create
    );

    public static Wire create(WireType type, BlockPos u, BlockPos v, float loadCoefficient) {
        var start = u.getCenter().toVector3f();
        var end = v.getCenter().toVector3f();
        var middle = start.add(end, new Vector3f()).mul(0.5f);
        return new Wire(type, ImmutableSet.of(u, v), u, v, start, end, middle, start.distance(end),
                new ChunkPos(SectionPos.blockToSectionCoord(middle.x), SectionPos.blockToSectionCoord(middle.z)),
                loadCoefficient);
    }

    public void drop(Level level, WireType type, DropRule dropRule) {
        switch (dropRule) {
            case NO_DROP -> {
            }
            case PARTIAL ->
                    Containers.dropItemStack(level, middle.x, middle.y, middle.z, new ItemStack(type.item(), Mth.floor(length * level.getRandom().nextFloat())));
            case FULL ->
                    Containers.dropItemStack(level, middle.x, middle.y, middle.z, new ItemStack(type.item(), Mth.floor(length)));
        }
    }

    public static int getItemCount(BlockPos u, BlockPos v) {
        return Mth.ceil(Math.sqrt(u.distSqr(v)));
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

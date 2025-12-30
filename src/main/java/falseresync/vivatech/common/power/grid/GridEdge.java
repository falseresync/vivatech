package falseresync.vivatech.common.power.grid;

import com.google.common.collect.ImmutableSortedSet;
import com.mojang.serialization.Codec;
import falseresync.vivatech.common.power.wire.Wire;
import falseresync.vivatech.common.power.wire.WireType;
import java.util.List;
import java.util.Objects;
import net.minecraft.core.BlockPos;

public record GridEdge(ImmutableSortedSet<BlockPos> positions, BlockPos u, BlockPos v) {
    public static final Codec<GridEdge> CODEC =
            Codec.list(BlockPos.CODEC, 2, 2).xmap(it -> new GridEdge(it.getFirst(), it.getLast()), it -> List.of(it.u, it.v));

    public GridEdge(BlockPos u, BlockPos v) {
        this(ImmutableSortedSet.of(u, v), u, v);
    }

    public Wire asWire(WireType wireType, float loadCoefficient) {
        return Wire.create(wireType, u, v, loadCoefficient);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GridEdge gridEdge)) return false;
        return Objects.equals(positions, gridEdge.positions);
    }

    @Override
    public int hashCode() {
        return positions.hashCode();
    }
}

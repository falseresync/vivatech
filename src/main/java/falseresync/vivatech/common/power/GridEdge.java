package falseresync.vivatech.common.power;

import com.google.common.collect.ImmutableSortedSet;
import com.mojang.serialization.Codec;
import falseresync.vivatech.common.power.wire.Wire;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.Objects;

public record GridEdge(ImmutableSortedSet<BlockPos> positions, BlockPos u, BlockPos v) {
    public static final Codec<GridEdge> CODEC =
            Codec.list(BlockPos.CODEC, 2, 2).xmap(it -> new GridEdge(it.getFirst(), it.getLast()), it -> List.of(it.u, it.v));

    public GridEdge(BlockPos u, BlockPos v) {
        this(ImmutableSortedSet.of(u, v), u, v);
    }

    public Wire toServerWire() {
        return Wire.createServerWire(u, v);
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

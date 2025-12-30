package falseresync.vivatech.common.power.grid;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public record GridVertex(BlockPos pos, Direction direction, @Nullable Appliance appliance) implements Comparable<GridVertex> {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GridVertex vertex)) return false;
        return pos.equals(vertex.pos) && appliance == vertex.appliance();
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos, appliance);
    }

    @Override
    public int compareTo(GridVertex o) {
        return pos.compareTo(o.pos);
    }
}

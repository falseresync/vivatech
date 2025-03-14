package falseresync.vivatech.common.power;

import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record GridVertex(BlockPos pos, @Nullable Appliance appliance) implements Comparable<GridVertex> {
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

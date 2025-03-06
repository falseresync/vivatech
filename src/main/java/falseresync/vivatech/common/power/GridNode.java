package falseresync.vivatech.common.power;

import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public record GridNode(BlockPos pos, @Nullable Appliance appliance) {
    @Override
    public int hashCode() {
        return pos.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GridNode that)) return false;
        return pos.equals(that.pos);
    }
}

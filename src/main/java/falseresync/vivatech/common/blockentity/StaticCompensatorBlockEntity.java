package falseresync.vivatech.common.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class StaticCompensatorBlockEntity extends BaseAppliance implements Ticking {
    public StaticCompensatorBlockEntity(BlockPos pos, BlockState state) {
        super(VivatechBlockEntities.STATIC_COMPENSATOR, pos, state);
    }

    @Override
    public void tick() {

    }
}

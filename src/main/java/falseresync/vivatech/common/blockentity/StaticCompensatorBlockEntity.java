package falseresync.vivatech.common.blockentity;

import falseresync.vivatech.common.blockentity.BaseAppliance;
import falseresync.vivatech.common.blockentity.Ticking;
import falseresync.vivatech.common.blockentity.VivatechBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class StaticCompensatorBlockEntity extends BaseAppliance implements Ticking {
    public StaticCompensatorBlockEntity(BlockPos pos, BlockState state) {
        super(VivatechBlockEntities.STATIC_COMPENSATOR, pos, state);
    }

    @Override
    public void tick() {

    }
}

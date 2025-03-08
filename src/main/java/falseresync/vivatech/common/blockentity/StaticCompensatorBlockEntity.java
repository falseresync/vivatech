package falseresync.vivatech.common.blockentity;

import falseresync.vivatech.common.power.Appliance;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class StaticCompensatorBlockEntity extends BlockEntity implements Ticking, Appliance {
    public StaticCompensatorBlockEntity(BlockPos pos, BlockState state) {
        super(VivatechBlockEntities.STATIC_COMPENSATOR, pos, state);
    }

    @Override
    public void tick() {

    }
}

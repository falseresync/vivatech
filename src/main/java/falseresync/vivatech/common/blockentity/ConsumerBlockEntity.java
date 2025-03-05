package falseresync.vivatech.common.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public class ConsumerBlockEntity extends BaseAppliance implements Ticking {
    public ConsumerBlockEntity(BlockPos pos, BlockState state) {
        super(VtBlockEntities.CONSUMER, pos, state);
    }

    @Override
    public void tick() {

    }

    @Override
    public void gridTick(float voltage) {
        super.gridTick(voltage);
    }

    @Override
    public float getGridCurrent() {
        return super.getGridCurrent();
    }
}

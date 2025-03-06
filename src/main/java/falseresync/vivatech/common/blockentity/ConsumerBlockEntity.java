package falseresync.vivatech.common.blockentity;

import falseresync.vivatech.common.power.Appliance;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ConsumerBlockEntity extends BlockEntity implements Ticking, Appliance {
    public ConsumerBlockEntity(BlockPos pos, BlockState state) {
        super(VtBlockEntities.CONSUMER, pos, state);
    }

    @Override
    public void tick() {

    }

    @Override
    public float getElectricalCurrent() {
        return -1;
    }

    @Override
    public void gridTick(float voltage) {
        Appliance.super.gridTick(voltage);
    }
}

package falseresync.vivatech.common.blockentity;

import falseresync.vivatech.common.block.GearboxBlock;
import falseresync.vivatech.common.block.VivatechBlocks;
import falseresync.vivatech.common.power.Appliance;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class GeneratorBlockEntity extends BaseAppliance implements Ticking {
    private boolean generating = true;

    public GeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(VivatechBlockEntities.GENERATOR, pos, state);
    }

    @Override
    public void tick() {
        if (!world.isClient) {
            return;
        }

        if (isConnected()) {
            var facing = getCachedState().get(GearboxBlock.FACING);
            var gearboxState = world.getBlockState(pos.offset(facing));
            var wind_turbineState = world.getBlockState(pos.offset(facing, 2));
            generating = gearboxState.isOf(VivatechBlocks.GEARBOX) && wind_turbineState.isOf(VivatechBlocks.WIND_TURBINE);
        } else if (generating) {
            generating = false;
        }
    }
    @Override
    public float getElectricalCurrent() {
        return generating ? 2 : 0;
    }
}

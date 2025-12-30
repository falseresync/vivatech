package falseresync.vivatech.common.blockentity;

import falseresync.vivatech.common.block.GearboxBlock;
import falseresync.vivatech.common.block.VivatechBlocks;
import falseresync.vivatech.common.blockentity.BaseAppliance;
import falseresync.vivatech.common.blockentity.Ticking;
import falseresync.vivatech.common.blockentity.VivatechBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class GeneratorBlockEntity extends BaseAppliance implements Ticking {
    private boolean generating = true;

    public GeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(VivatechBlockEntities.GENERATOR, pos, state);
    }

    @Override
    public void tick() {
        if (!level.isClientSide) {
            return;
        }

        if (isConnected()) {
            var facing = getBlockState().getValue(GearboxBlock.FACING);
            var gearboxState = level.getBlockState(worldPosition.relative(facing));
            var wind_turbineState = level.getBlockState(worldPosition.relative(facing, 2));
            generating = gearboxState.is(VivatechBlocks.GEARBOX) && wind_turbineState.is(VivatechBlocks.WIND_TURBINE);
        } else if (generating) {
            generating = false;
        }
    }
    @Override
    public float getElectricalCurrent() {
        return generating ? 2 : 0;
    }
}

package falseresync.vivatech.world.blockentity;

import falseresync.vivatech.world.block.GearboxBlock;
import falseresync.vivatech.world.block.VivatechBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class GeneratorBlockEntity extends BaseAppliance implements Ticking {
    private boolean generating = true;

    public GeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(VivatechBlockEntities.GENERATOR, pos, state);
    }

    @Override
    public void tick() {
        if (!level.isClientSide()) {
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

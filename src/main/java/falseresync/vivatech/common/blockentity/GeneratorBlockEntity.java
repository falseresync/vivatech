package falseresync.vivatech.common.blockentity;

import falseresync.vivatech.common.block.GearboxBlock;
import falseresync.vivatech.common.block.VivatechBlocks;
import falseresync.vivatech.common.power.Appliance;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class GeneratorBlockEntity extends BlockEntity implements Ticking, Appliance {
    private boolean generating = true;
    private boolean connected = false;

    public GeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(VivatechBlockEntities.GENERATOR, pos, state);
    }

    @Override
    public void tick() {
        if (!world.isClient) {
            return;
        }

        if (connected) {
            var facing = getCachedState().get(GearboxBlock.FACING);
            var gearboxState = world.getBlockState(pos.offset(facing));
            var windmillState = world.getBlockState(pos.offset(facing, 2));
            generating = gearboxState.isOf(VivatechBlocks.GEARBOX) && windmillState.isOf(VivatechBlocks.WINDMILL);
        }
    }

    @Override
    public float getElectricalCurrent() {
        return generating ? 2 : 0;
    }

    @Override
    public void onGridConnected() {
        connected = true;
    }

    @Override
    public void onGridDisconnected() {
        connected = false;
        generating = false;
    }
}

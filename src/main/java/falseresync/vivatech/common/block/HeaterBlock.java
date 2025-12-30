package falseresync.vivatech.common.block;

import falseresync.vivatech.common.block.BaseBlockWithEntity;
import falseresync.vivatech.common.block.RestrictsWirePostPlacement;
import falseresync.vivatech.common.blockentity.HeaterBlockEntity;
import falseresync.vivatech.common.blockentity.VivatechBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class HeaterBlock extends BaseBlockWithEntity.WithTicker implements RestrictsWirePostPlacement.AllowVertical {
    protected HeaterBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborChanged(state, world, pos, sourceBlock, sourcePos, notify);
        if (!world.isClientSide) {
            if (world.getBlockEntity(pos) instanceof HeaterBlockEntity heater) {
                heater.scheduleScan();
            }
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new HeaterBlockEntity(pos, state);
    }

    @Override
    protected BlockEntityType<HeaterBlockEntity> getBlockEntityType() {
        return VivatechBlockEntities.HEATER;
    }
}

package falseresync.vivatech.common.block;

import falseresync.vivatech.common.blockentity.HeaterBlockEntity;
import falseresync.vivatech.common.blockentity.VivatechBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HeaterBlock extends BaseBlockWithEntity.WithTicker implements RestrictsWirePostPlacement.AllowVertical {
    protected HeaterBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        if (!world.isClient) {
            if (world.getBlockEntity(pos) instanceof HeaterBlockEntity heater) {
                heater.scheduleScan();
            }
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new HeaterBlockEntity(pos, state);
    }

    @Override
    protected BlockEntityType<HeaterBlockEntity> getBlockEntityType() {
        return VivatechBlockEntities.HEATER;
    }
}

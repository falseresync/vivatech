package falseresync.vivatech.world.block;

import falseresync.vivatech.world.blockentity.HeaterBlockEntity;
import falseresync.vivatech.world.blockentity.VivatechBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;
import org.jspecify.annotations.Nullable;

import java.util.Properties;

public class HeaterBlock extends BaseBlockWithEntity.WithTicker implements RestrictsWirePostPlacement.AllowVertical {
    protected HeaterBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, @Nullable Orientation orientation, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, block, orientation, movedByPiston);
        if (!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof HeaterBlockEntity heater) {
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

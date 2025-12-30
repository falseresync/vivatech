package falseresync.vivatech.common.block;

import falseresync.vivatech.common.block.BaseBlockWithEntity;
import falseresync.vivatech.common.block.RestrictsWirePostPlacement;
import falseresync.vivatech.common.blockentity.StaticCompensatorBlockEntity;
import falseresync.vivatech.common.blockentity.VivatechBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class StaticCompensatorBlock extends BaseBlockWithEntity.WithTicker implements RestrictsWirePostPlacement.AllowVertical {
    protected StaticCompensatorBlock(Properties settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StaticCompensatorBlockEntity(pos, state);
    }

    @Override
    protected BlockEntityType<StaticCompensatorBlockEntity> getBlockEntityType() {
        return VivatechBlockEntities.STATIC_COMPENSATOR;
    }
}

package falseresync.vivatech.common.block;

import falseresync.vivatech.common.blockentity.StaticCompensatorBlockEntity;
import falseresync.vivatech.common.blockentity.VivatechBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class StaticCompensatorBlock extends BaseBlockWithEntity.WithTicker implements RestrictsWirePostPlacement.AllowVertical {
    protected StaticCompensatorBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new StaticCompensatorBlockEntity(pos, state);
    }

    @Override
    protected BlockEntityType<StaticCompensatorBlockEntity> getBlockEntityType() {
        return VivatechBlockEntities.STATIC_COMPENSATOR;
    }
}

package falseresync.vivatech.common.block;

import com.mojang.serialization.MapCodec;
import falseresync.vivatech.common.blockentity.ConsumerBlockEntity;
import falseresync.vivatech.common.blockentity.Ticking;
import falseresync.vivatech.common.blockentity.VtBlockEntities;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ConsumerBlock extends BlockWithEntity {
    public static final MapCodec<ConsumerBlock> CODEC = createCodec(ConsumerBlock::new);

    protected ConsumerBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<ConsumerBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ConsumerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, VtBlockEntities.CONSUMER, Ticking.getDefaultTicker());
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}

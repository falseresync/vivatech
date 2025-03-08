package falseresync.vivatech.common.block;

import com.mojang.serialization.MapCodec;
import falseresync.vivatech.common.blockentity.WindmillBlockEntity;
import falseresync.vivatech.common.blockentity.Ticking;
import falseresync.vivatech.common.blockentity.VtBlockEntities;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class WindmillBlock extends BlockWithEntity {
    public static final MapCodec<WindmillBlock> CODEC = createCodec(WindmillBlock::new);

    protected WindmillBlock(AbstractBlock.Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<WindmillBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new WindmillBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, VtBlockEntities.WINDMILL, Ticking.getDefaultTicker());
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}

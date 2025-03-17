package falseresync.vivatech.common.block;

import com.mojang.serialization.MapCodec;
import falseresync.vivatech.common.blockentity.Ticking;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.OverrideOnly
public class BaseBlockWithEntity extends BlockWithEntity {
    public static final MapCodec<BaseBlockWithEntity> CODEC = createCodec(BaseBlockWithEntity::new);

    protected BaseBlockWithEntity(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<BaseBlockWithEntity> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public static abstract class WithTicker extends BaseBlockWithEntity {
        protected WithTicker(Settings settings) {
            super(settings);
        }

        @Nullable
        @Override
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
            return validateTicker(type, getBlockEntityType(), Ticking.getDefaultTicker());
        }

        protected abstract BlockEntityType<?> getBlockEntityType();
    }
}

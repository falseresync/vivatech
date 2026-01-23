package falseresync.vivatech.world.block;

import com.mojang.serialization.MapCodec;
import falseresync.vivatech.world.blockentity.Ticking;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.OverrideOnly
public class BaseBlockWithEntity extends BaseEntityBlock {
    public static final MapCodec<BaseBlockWithEntity> CODEC = simpleCodec(BaseBlockWithEntity::new);

    protected BaseBlockWithEntity(Properties settings) {
        super(settings);
    }

    @Override
    protected MapCodec<BaseBlockWithEntity> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public static abstract class WithTicker extends BaseBlockWithEntity {
        protected WithTicker(Properties settings) {
            super(settings);
        }

        @Nullable
        @Override
        public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
            return createTickerHelper(type, getBlockEntityType(), Ticking.getDefaultTicker());
        }

        protected abstract BlockEntityType<?> getBlockEntityType();
    }
}

package falseresync.vivatech.common.block;

import com.mojang.serialization.MapCodec;
import falseresync.vivatech.common.blockentity.GeneratorBlockEntity;
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

public class GeneratorBlock extends BlockWithEntity {
    public static final MapCodec<GeneratorBlock> CODEC = createCodec(GeneratorBlock::new);

    protected GeneratorBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<GeneratorBlock> getCodec() {
        return CODEC;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GeneratorBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, VtBlockEntities.GENERATOR, Ticking.getDefaultTicker());
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}

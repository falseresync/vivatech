package falseresync.vivatech.common.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class StaticCompensatorBlock extends BlockWithEntity {
    public static final MapCodec<StaticCompensatorBlock> CODEC = createCodec(StaticCompensatorBlock::new);

    protected StaticCompensatorBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<StaticCompensatorBlock> getCodec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }
}

package falseresync.vivatech.common.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class GeneratorBlockEntity extends BlockEntity implements Ticking {
    public GeneratorBlockEntity(BlockPos pos, BlockState state) {
        super(VtBlockEntities.GENERATOR, pos, state);
    }

    @Override
    public void tick() {

    }
}

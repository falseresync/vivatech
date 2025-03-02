package falseresync.vivatech.common.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ConsumerBlockEntity extends BlockEntity implements Ticking {
    public ConsumerBlockEntity(BlockPos pos, BlockState state) {
        super(VtBlockEntities.CONSUMER, pos, state);
    }

    @Override
    public void tick() {

    }
}

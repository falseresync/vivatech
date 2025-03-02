package falseresync.vivatech.common.blockentity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;

public interface Ticking {
    void tick();

    static <T extends BlockEntity> BlockEntityTicker<T> getDefaultTicker() {
        return (_0, _1, _2, it) -> ((Ticking) it).tick();
    }
}

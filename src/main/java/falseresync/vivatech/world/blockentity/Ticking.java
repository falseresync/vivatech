package falseresync.vivatech.world.blockentity;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

public interface Ticking {
    void tick();

    static <T extends BlockEntity> BlockEntityTicker<T> getDefaultTicker() {
        return (_0, _1, _2, it) -> ((Ticking) it).tick();
    }
}

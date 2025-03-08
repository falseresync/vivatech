package falseresync.vivatech.common.world;

import net.minecraft.block.*;
import net.minecraft.util.math.*;
import net.minecraft.world.*;
import net.minecraft.world.explosion.*;

public class VivatechWorld {
    public static class DischargeExplosionBehavior extends ExplosionBehavior {
        public static final DischargeExplosionBehavior INSTANCE = new DischargeExplosionBehavior();

        @Override
        public boolean canDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float power) {
            return false;
        }
    }
}

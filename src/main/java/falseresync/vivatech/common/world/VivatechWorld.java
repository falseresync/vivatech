package falseresync.vivatech.common.world;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.block.state.BlockState;

public class VivatechWorld {
    public static class DischargeExplosionBehavior extends ExplosionDamageCalculator {
        public static final DischargeExplosionBehavior INSTANCE = new DischargeExplosionBehavior();

        @Override
        public boolean shouldBlockExplode(Explosion explosion, BlockGetter world, BlockPos pos, BlockState state, float power) {
            return false;
        }
    }
}

package falseresync.vivatech.common.blockentity;

import falseresync.vivatech.common.blockentity.Ticking;
import falseresync.vivatech.common.blockentity.VivatechBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WindTurbineBlockEntity extends BlockEntity implements Ticking {
    private static final float rotationSpeed = 0.5f / 360;
    private float lastRotationProgress;
    private float rotationProgress;

    public WindTurbineBlockEntity(BlockPos pos, BlockState state) {
        super(VivatechBlockEntities.WIND_TURBINE, pos, state);
    }

    @Override
    public void tick() {
        if (lastRotationProgress >= 360) {
            rotationProgress -= 360;
        }
        lastRotationProgress = rotationProgress;
        rotationProgress += rotationSpeed;
    }

    public float getRotationProgress(float tickDelta) {
        return Mth.lerp(tickDelta, lastRotationProgress, rotationProgress);
    }
}

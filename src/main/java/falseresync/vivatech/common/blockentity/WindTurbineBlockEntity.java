package falseresync.vivatech.common.blockentity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

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
        return MathHelper.lerp(tickDelta, lastRotationProgress, rotationProgress);
    }
}

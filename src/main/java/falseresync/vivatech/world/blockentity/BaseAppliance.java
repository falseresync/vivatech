package falseresync.vivatech.world.blockentity;

import com.google.common.base.Preconditions;
import falseresync.vivatech.world.electricity.grid.Appliance;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseAppliance extends BlockEntity implements Appliance {
    private float minAcceptableVoltage = 210;
    private float maxAcceptableVoltage = 250;
    private boolean connected = false;
    private boolean frozen = false;
    private boolean operational = false;

    public BaseAppliance(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public BlockPos getAppliancePos() {
        return getBlockPos();
    }

    @Override
    public void onGridConnected() {
        connected = true;
    }

    @Override
    public void onGridDisconnected() {
        connected = false;
    }

    @Override
    public void onGridFrozen() {
        frozen = true;
    }

    @Override
    public void onGridUnfrozen() {
        frozen = false;
    }

    @Override
    public void gridTick(float voltage) {
        operational = voltage >= minAcceptableVoltage && voltage <= maxAcceptableVoltage;
    }

    protected void setAcceptableVoltage(float min, float max) {
        Preconditions.checkArgument(min < max, "Max acceptable voltage must be greater than min");
        minAcceptableVoltage = min;
        maxAcceptableVoltage = max;
    }

    public float getMinAcceptableVoltage() {
        return minAcceptableVoltage;
    }

    public float getMaxAcceptableVoltage() {
        return maxAcceptableVoltage;
    }

    protected boolean isOperational() {
        return connected && operational && !frozen;
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isFrozen() {
        return frozen;
    }
}

package falseresync.vivatech.common.blockentity;

import com.google.common.base.Preconditions;
import falseresync.vivatech.common.power.Appliance;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;

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
        return getPos();
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

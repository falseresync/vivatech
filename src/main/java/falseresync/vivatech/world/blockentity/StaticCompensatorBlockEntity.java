package falseresync.vivatech.world.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class StaticCompensatorBlockEntity extends BaseAppliance implements Ticking {
    private int remainingCharge = 0;

    public StaticCompensatorBlockEntity(BlockPos pos, BlockState state) {
        super(VivatechBlockEntities.STATIC_COMPENSATOR, pos, state);
    }

    @Override
    public void tick() {
        if (isOperational()) {
            remainingCharge += 1;

        }
    }

    @Override
    public float getElectricalCurrent() {
        return super.getElectricalCurrent();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("remaining_charge", remainingCharge);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.getInt("remaining_charge").ifPresent(value -> remainingCharge = value);
    }
}

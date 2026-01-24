package falseresync.vivatech.world.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.TriState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class StaticCompensatorBlockEntity extends BaseAppliance implements Ticking {
    private int storedEnergy = 0;
    private TriState generating;

    public StaticCompensatorBlockEntity(BlockPos pos, BlockState state) {
        super(VivatechBlockEntities.STATIC_COMPENSATOR, pos, state);
    }

    @Override
    public void tick() {
    }

    @Override
    public void gridTick(float voltage) {
        super.gridTick(voltage);
        if (voltage < getMinAcceptableVoltage() + 2) {
            generating = TriState.TRUE;
        } else if (voltage > getMaxAcceptableVoltage() - 2) {
            generating = TriState.FALSE;
        } else {
            generating = TriState.DEFAULT;
            storedEnergy += 1;
        }
    }

    @Override
    public float getElectricalCurrent() {
        if (storedEnergy > 0) {
            return switch (generating) {
                case TRUE -> 1;
                case FALSE -> -1;
                case DEFAULT -> 0;
            };
        }
        return super.getElectricalCurrent();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("stored_energy", storedEnergy);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.getInt("stored_energy").ifPresent(value -> storedEnergy = value);
    }
}

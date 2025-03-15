package falseresync.vivatech.common.blockentity;

import falseresync.vivatech.common.power.Appliance;
import falseresync.vivatech.common.power.ApplianceProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class ContactorBlockEntity extends BlockEntity implements ApplianceProvider {
    private final Appliance applianceA = new Appliance() {
        @Override
        public BlockPos getAppliancePos() {
            return null;
        }
    };

    public ContactorBlockEntity(BlockPos pos, BlockState state) {
        super(VivatechBlockEntities.CONTACTOR, pos, state);
    }

    @Override
    public Appliance getAppliance(@Nullable Direction side) {
        return null;
    }
}

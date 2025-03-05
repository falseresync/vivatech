package falseresync.vivatech.common.blockentity;

import falseresync.vivatech.common.block.WirePostBlock;
import falseresync.vivatech.common.power.Appliance;
import falseresync.vivatech.common.power.ApplianceProxy;
import falseresync.vivatech.common.power.PowerSystem;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class WirePostBlockEntity extends BaseAppliance implements Ticking, ApplianceProxy {
    private @Nullable Appliance proxiedAppliance;

    public WirePostBlockEntity(BlockPos pos, BlockState state) {
        super(VtBlockEntities.WIRE_POST, pos, state);
    }

    @Override
    public void tick() {
    }

    @Override
    public void onGridConnected() {
        proxiedAppliance = PowerSystem.APPLIANCE.find(world, pos.offset(getCachedState().get(WirePostBlock.FACING)), null);
        if (proxiedAppliance != null) {
            proxiedAppliance.onGridConnected();
        }
    }

    @Override
    public void onGridDisconnected() {
        if (proxiedAppliance != null) {
            proxiedAppliance.onGridConnected();
        }
    }

    @Override
    @Nullable
    public Appliance getProxiedAppliance() {
        return proxiedAppliance;
    }
}

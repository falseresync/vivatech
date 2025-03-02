package falseresync.vivatech.common.item;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.power.Appliance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;

public class ConnectorItem extends WireManagementItem {
    public ConnectorItem(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult manageWire(World world, Appliance applianceU, Appliance applianceV, GlobalPos anchor, BlockPos currentPos) {
        if (!world.isClient) {
            var gridsManager = Vivatech.getServerGridsLoader().getGridsManager(world);
            var grid = gridsManager.findOrCreate(applianceU.getGridUuid(), applianceV.getGridUuid());
            grid.add(anchor.pos(), currentPos);
            return ActionResult.CONSUME;
        }

        return ActionResult.SUCCESS;
    }
}

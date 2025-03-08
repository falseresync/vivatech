package falseresync.vivatech.network.report;

import falseresync.lib.registry.RegistryObject;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;

import static falseresync.vivatech.common.Vivatech.vtId;

public class VivatechReports {
    public static final SimpleRegistry<Report> REGISTRY =
            FabricRegistryBuilder
                    .<Report>createSimple(RegistryKey.ofRegistry(vtId("reports")))
                    .attribute(RegistryAttribute.SYNCED)
                    .buildAndRegister();

    public static final @RegistryObject WandInsufficientChargeReport WAND_INSUFFICIENT_CHARGE = new WandInsufficientChargeReport();

    public static final @RegistryObject CometWarpNoAnchorReport COMET_WARP_NO_ANCHOR = new CometWarpNoAnchorReport();
    public static final @RegistryObject CometWarpAnchorPlacedReport COMET_WARP_ANCHOR_PLACED = new CometWarpAnchorPlacedReport();
    public static final @RegistryObject CometWarpTeleportedReport COMET_WARP_TELEPORTED = new CometWarpTeleportedReport();
}

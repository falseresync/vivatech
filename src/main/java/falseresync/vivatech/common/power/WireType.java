package falseresync.vivatech.common.power;

import falseresync.lib.registry.RegistryObject;
import falseresync.vivatech.common.item.VivatechItems;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.item.Item;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

import static falseresync.vivatech.common.Vivatech.vtId;

public record WireType(Item item, int voltage, int maxCurrent, int overcurrentToleranceTime) {
    public static final Registry<WireType> REGISTRY =
            FabricRegistryBuilder.<WireType>createSimple(RegistryKey.ofRegistry(vtId("wire_types")))
                    .attribute(RegistryAttribute.MODDED)
                    .attribute(RegistryAttribute.SYNCED)
                    .buildAndRegister();

    public static final @RegistryObject WireType V_230 = new WireType(VivatechItems.WIRE, 230, 32, 100);
}

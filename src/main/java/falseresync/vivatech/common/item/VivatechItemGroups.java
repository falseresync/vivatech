package falseresync.vivatech.common.item;

import falseresync.lib.registry.RegistryObject;
import falseresync.vivatech.common.item.VivatechItems;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;

public class VivatechItemGroups {
    public static final @RegistryObject CreativeModeTab GENERAL =
            FabricItemGroup.builder()
                    .icon(falseresync.vivatech.common.item.VivatechItems.PROBE::getDefaultInstance)
                    .title(Component.translatable("itemGroup.vivatech.general"))
                    .displayItems((displayContext, entries) -> {
                        entries.accept(falseresync.vivatech.common.item.VivatechItems.GENERATOR);
                        entries.accept(falseresync.vivatech.common.item.VivatechItems.GEARBOX);
                        entries.accept(falseresync.vivatech.common.item.VivatechItems.WIND_TURBINE);

                        entries.accept(falseresync.vivatech.common.item.VivatechItems.HEATER);
                        entries.accept(falseresync.vivatech.common.item.VivatechItems.CHARGER);

                        entries.accept(falseresync.vivatech.common.item.VivatechItems.STATIC_COMPENSATOR);
                        entries.accept(falseresync.vivatech.common.item.VivatechItems.CONTACTOR);

                        entries.accept(falseresync.vivatech.common.item.VivatechItems.WIRE_POST);

                        entries.accept(falseresync.vivatech.common.item.VivatechItems.WIRE);
                        entries.accept(falseresync.vivatech.common.item.VivatechItems.MORTAR_AND_PESTLE);
                        entries.accept(falseresync.vivatech.common.item.VivatechItems.SCREWDRIVER);
                        entries.accept(falseresync.vivatech.common.item.VivatechItems.PLIERS);
                        entries.accept(falseresync.vivatech.common.item.VivatechItems.PROBE);
                        entries.accept(falseresync.vivatech.common.item.VivatechItems.INSPECTOR_GOGGLES);
                        entries.accept(falseresync.vivatech.common.item.VivatechItems.FOCUSES_POUCH);

                        entries.accept(falseresync.vivatech.common.item.VivatechItems.GADGET);

                        entries.accept(falseresync.vivatech.common.item.VivatechItems.STARSHOOTER_FOCUS);
                        entries.accept(falseresync.vivatech.common.item.VivatechItems.LIGHTNING_FOCUS);
                        entries.accept(falseresync.vivatech.common.item.VivatechItems.COMET_WARP_FOCUS);
                        entries.accept(VivatechItems.ENERGY_VEIL_FOCUS);
                    })
                    .build();
}

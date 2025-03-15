package falseresync.vivatech.common.item;

import falseresync.lib.registry.RegistryObject;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class VivatechItemGroups {
    public static final @RegistryObject ItemGroup GENERAL =
            FabricItemGroup.builder()
                    .icon(VivatechItems.PROBE::getDefaultStack)
                    .displayName(Text.translatable("itemGroup.vivatech.general"))
                    .entries((displayContext, entries) -> {
                        entries.add(VivatechItems.GENERATOR);
                        entries.add(VivatechItems.GEARBOX);
                        entries.add(VivatechItems.WIND_TURBINE);

                        entries.add(VivatechItems.HEATER);
                        entries.add(VivatechItems.CHARGER);

                        entries.add(VivatechItems.STATIC_COMPENSATOR);
                        entries.add(VivatechItems.CONTACTOR);

                        entries.add(VivatechItems.WIRE_POST);

                        entries.add(VivatechItems.WIRE);
                        entries.add(VivatechItems.MORTAR_AND_PESTLE);
                        entries.add(VivatechItems.PLIERS);
                        entries.add(VivatechItems.PROBE);
                        entries.add(VivatechItems.INSPECTOR_GOGGLES);
                        entries.add(VivatechItems.FOCUSES_POUCH);

                        entries.add(VivatechItems.GADGET);

                        entries.add(VivatechItems.STARSHOOTER_FOCUS);
                        entries.add(VivatechItems.LIGHTNING_FOCUS);
                        entries.add(VivatechItems.COMET_WARP_FOCUS);
                        entries.add(VivatechItems.ENERGY_VEIL_FOCUS);
                    })
                    .build();
}

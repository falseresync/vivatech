package falseresync.vivatech.world.item;

import falseresync.vivatech.Vivatech;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;

public class VivatechCreativeTabs {
    public static final CreativeModeTab GENERAL =
            FabricCreativeModeTab.builder()
                    .icon(VivatechItems.PROBE::getDefaultInstance)
                    .title(Component.translatable("itemGroup.vivatech.general"))
                    .displayItems((displayContext, entries) -> {
                        entries.accept(VivatechItems.GENERATOR);
                        entries.accept(VivatechItems.GEARBOX);
                        entries.accept(VivatechItems.WIND_TURBINE);

                        entries.accept(VivatechItems.HEATER);

                        entries.accept(VivatechItems.STATIC_COMPENSATOR);
                        entries.accept(VivatechItems.CONTACTOR);

                        entries.accept(VivatechItems.WIRE_POST);

                        entries.accept(VivatechItems.WIRE);
                        entries.accept(VivatechItems.MORTAR_AND_PESTLE);
                        entries.accept(VivatechItems.SCREWDRIVER);
                        entries.accept(VivatechItems.PLIERS);
                        entries.accept(VivatechItems.PROBE);
                    })
                    .build();

    public static void init() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Vivatech.id("general"), GENERAL);
    }
}

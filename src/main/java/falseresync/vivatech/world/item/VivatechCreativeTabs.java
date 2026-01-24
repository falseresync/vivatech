package falseresync.vivatech.world.item;

import falseresync.vivatech.Vivatech;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Items;

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

                        entries.accept(VivatechItems.MACHINE_CHASSIS);

                        entries.accept(VivatechItems.COPPER_WIRE);
                        entries.accept(VivatechItems.CUPROSTEEL_WIRE);
                        entries.accept(VivatechItems.THICK_CURPOSTEEL_WIRE);
                        entries.accept(VivatechItems.MORTAR_AND_PESTLE);
                        entries.accept(VivatechItems.SCREWDRIVER);
                        entries.accept(VivatechItems.PLIERS);
                        entries.accept(VivatechItems.PROBE);
                    })
                    .build();

    public static void init() {
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Vivatech.id("general"), GENERAL);

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(output -> {
            output.insertAfter(Items.COPPER_INGOT, VivatechItems.CUPROSTEEL_INGOT, VivatechItems.BRASS_INGOT);
            output.insertAfter(Items.COPPER_NUGGET, VivatechItems.CUPROSTEEL_NUGGET, VivatechItems.BRASS_NUGGET);
        });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.BUILDING_BLOCKS).register(output -> {
            output.insertBefore(Items.GOLD_BLOCK, VivatechItems.CUPROSTEEL_BLOCK, VivatechItems.BRASS_BLOCK);
        });
    }
}

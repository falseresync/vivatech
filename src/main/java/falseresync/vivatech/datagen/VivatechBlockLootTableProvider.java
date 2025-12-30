package falseresync.vivatech.datagen;

import falseresync.vivatech.common.block.VivatechBlocks;
import falseresync.vivatech.common.item.VivatechItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.core.HolderLookup;
import java.util.concurrent.CompletableFuture;

public class VivatechBlockLootTableProvider extends FabricBlockLootTableProvider {
    protected VivatechBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(dataOutput, registriesFuture);
    }

    @Override
    public void generate() {
        add(VivatechBlocks.GENERATOR, createSingleItemTable(VivatechItems.GENERATOR));
        add(VivatechBlocks.GEARBOX, createSingleItemTable(VivatechItems.GEARBOX));
        add(VivatechBlocks.WIND_TURBINE, createSingleItemTable(VivatechItems.WIND_TURBINE));
        add(VivatechBlocks.HEATER, createSingleItemTable(VivatechItems.HEATER));
        add(VivatechBlocks.STATIC_COMPENSATOR, createSingleItemTable(VivatechItems.STATIC_COMPENSATOR));
        add(VivatechBlocks.CHARGER, createSingleItemTable(VivatechItems.CHARGER));
        add(VivatechBlocks.WIRE_POST, createSingleItemTable(VivatechItems.WIRE_POST));
    }
}

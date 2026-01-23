package falseresync.vivatech.data;

import falseresync.vivatech.world.block.VivatechBlocks;
import falseresync.vivatech.world.item.VivatechItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class VivatechBlockLootProvider extends FabricBlockLootSubProvider {
    protected VivatechBlockLootProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(dataOutput, registriesFuture);
    }

    @Override
    public void generate() {
        add(VivatechBlocks.GENERATOR, createSingleItemTable(VivatechItems.GENERATOR));
        add(VivatechBlocks.GEARBOX, createSingleItemTable(VivatechItems.GEARBOX));
        add(VivatechBlocks.WIND_TURBINE, createSingleItemTable(VivatechItems.WIND_TURBINE));
        add(VivatechBlocks.HEATER, createSingleItemTable(VivatechItems.HEATER));
        add(VivatechBlocks.STATIC_COMPENSATOR, createSingleItemTable(VivatechItems.STATIC_COMPENSATOR));
        add(VivatechBlocks.WIRE_POST, createSingleItemTable(VivatechItems.WIRE_POST));
    }
}

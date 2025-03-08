package falseresync.vivatech.datagen;

import falseresync.vivatech.common.block.VivatechBlocks;
import falseresync.vivatech.common.item.VivatechItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class VivatechBlockLootTableProvider extends FabricBlockLootTableProvider {
    protected VivatechBlockLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(dataOutput, registriesFuture);
    }

    @Override
    public void generate() {
        addDrop(VivatechBlocks.GENERATOR, drops(VivatechItems.GENERATOR));
        addDrop(VivatechBlocks.GEARBOX, drops(VivatechItems.GEARBOX));
        addDrop(VivatechBlocks.WINDMILL, drops(VivatechItems.WINDMILL));
        addDrop(VivatechBlocks.HEATER, drops(VivatechItems.HEATER));
        addDrop(VivatechBlocks.STATIC_COMPENSATOR, drops(VivatechItems.STATIC_COMPENSATOR));
        addDrop(VivatechBlocks.CHARGER, drops(VivatechItems.CHARGER));
        addDrop(VivatechBlocks.WIRE_POST, drops(VivatechItems.WIRE_POST));
    }
}

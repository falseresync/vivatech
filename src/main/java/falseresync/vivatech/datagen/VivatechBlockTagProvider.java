package falseresync.vivatech.datagen;

import falseresync.vivatech.common.block.VivatechBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import java.util.concurrent.CompletableFuture;

public class VivatechBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public VivatechBlockTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(VivatechBlocks.GENERATOR)
                .add(VivatechBlocks.GEARBOX)
                .add(VivatechBlocks.WIND_TURBINE)
                .add(VivatechBlocks.HEATER)
                .add(VivatechBlocks.STATIC_COMPENSATOR)
                .add(VivatechBlocks.CHARGER)
                .add(VivatechBlocks.WIRE_POST);
    }
}

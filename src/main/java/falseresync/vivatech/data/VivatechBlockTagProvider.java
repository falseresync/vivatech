package falseresync.vivatech.data;

import falseresync.vivatech.world.block.VivatechBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;

import java.util.concurrent.CompletableFuture;

public class VivatechBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
    public VivatechBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        valueLookupBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(VivatechBlocks.GENERATOR)
                .add(VivatechBlocks.GEARBOX)
                .add(VivatechBlocks.WIND_TURBINE)
                .add(VivatechBlocks.HEATER)
                .add(VivatechBlocks.STATIC_COMPENSATOR)
                .add(VivatechBlocks.WIRE_POST);
    }
}

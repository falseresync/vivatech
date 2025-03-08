package falseresync.vivatech.datagen;

import falseresync.vivatech.common.block.VivatechBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class VivatechBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public VivatechBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(BlockTags.PICKAXE_MINEABLE)
                .add(VivatechBlocks.GENERATOR)
                .add(VivatechBlocks.GEARBOX)
                .add(VivatechBlocks.WINDMILL)
                .add(VivatechBlocks.HEATER)
                .add(VivatechBlocks.STATIC_COMPENSATOR)
                .add(VivatechBlocks.CHARGER)
                .add(VivatechBlocks.WIRE_POST);
    }
}

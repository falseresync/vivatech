package falseresync.vivatech.datagen;

import falseresync.vivatech.common.item.VivatechItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public final class VivatechModelGenerator extends FabricModelProvider {
    public VivatechModelGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(VivatechItems.RAW_ZINC, Models.GENERATED);
        itemModelGenerator.register(VivatechItems.ZINC_INGOT, Models.GENERATED);
        itemModelGenerator.register(VivatechItems.ZINC_NUGGET, Models.GENERATED);
        itemModelGenerator.register(VivatechItems.SACRIFICIAL_DAGGER, Models.GENERATED);
        itemModelGenerator.register(VivatechItems.LIFESSENCE_ACCUMULATOR, Models.GENERATED);
    }
}

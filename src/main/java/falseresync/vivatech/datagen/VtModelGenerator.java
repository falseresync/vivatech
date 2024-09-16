package falseresync.vivatech.datagen;

import falseresync.vivatech.item.VtItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.BlockStateModelGenerator;
import net.minecraft.data.client.ItemModelGenerator;
import net.minecraft.data.client.Models;

public final class VtModelGenerator extends FabricModelProvider {
    public VtModelGenerator(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {

    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(VtItems.RAW_ZINC, Models.GENERATED);
        itemModelGenerator.register(VtItems.ZINC_INGOT, Models.GENERATED);
        itemModelGenerator.register(VtItems.ZINC_NUGGET, Models.GENERATED);
        itemModelGenerator.register(VtItems.SACRIFICIAL_DAGGER, Models.GENERATED);
    }
}

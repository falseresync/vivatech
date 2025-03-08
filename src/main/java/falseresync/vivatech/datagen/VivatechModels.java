package falseresync.vivatech.datagen;

import falseresync.vivatech.common.block.VtBlocks;
import falseresync.vivatech.common.item.VtItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;

public class VivatechModels extends FabricModelProvider {
    private BlockStateModelGenerator blockStateModelGenerator;

    public VivatechModels(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        this.blockStateModelGenerator = blockStateModelGenerator;

        blockStateModelGenerator.registerNorthDefaultHorizontalRotated(VtBlocks.GENERATOR, TexturedModel.CUBE_COLUMN_HORIZONTAL);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotated(VtBlocks.GEARBOX, TexturedModel.CUBE_COLUMN_HORIZONTAL);

        var windmillModelId = TexturedModel.PARTICLE.upload(VtBlocks.WINDMILL, blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier
                        .create(
                                VtBlocks.WINDMILL,
                                BlockStateVariant.create().put(VariantSettings.MODEL, windmillModelId)
                        ).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates())
        );
        blockStateModelGenerator.excludeFromSimpleItemModelGeneration(VtBlocks.WINDMILL);

        blockStateModelGenerator.registerSingleton(VtBlocks.HEATER, TexturedModel.CUBE_BOTTOM_TOP);

        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier
                        .create(
                                VtBlocks.WIRE_POST,
                                BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(VtBlocks.WIRE_POST))
                        ).coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates())
        );
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(VtItems.WINDMILL, Models.GENERATED);

        itemModelGenerator.register(VtItems.CONNECTOR, Models.GENERATED);
        itemModelGenerator.register(VtItems.PLIERS, Models.GENERATED);
    }
}

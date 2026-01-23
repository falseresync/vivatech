package falseresync.vivatech.data;

import falseresync.vivatech.Vivatech;
import falseresync.vivatech.world.block.VivatechBlocks;
import falseresync.vivatech.world.item.VivatechItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.*;

import java.util.Optional;
import java.util.function.Consumer;

import static net.minecraft.client.data.models.BlockModelGenerators.plainVariant;
import static net.minecraft.client.data.models.model.TexturedModel.createDefault;

public class VivatechModelProvider extends FabricModelProvider {
    public static class Templates {
        public static final ModelTemplate ADEQUATE_CUBE_COLUMN_HORIZONTAL
                = new ModelTemplate(Optional.of(Vivatech.id("block/cube_column_horizontal")), Optional.empty(), TextureSlot.SIDE, TextureSlot.END);
    }

    public static class TexturedModelProviders {
        public static final TexturedModel.Provider ADEQUATE_CUBE_COLUMN_HORIZONTAL
                = createDefault(TextureMapping::column, Templates.ADEQUATE_CUBE_COLUMN_HORIZONTAL);
    }

    private BlockModelGenerators blockStateModelGenerator;
    private Consumer<BlockModelDefinitionGenerator> blockStateOutput;

    public VivatechModelProvider(FabricPackOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        this.blockStateModelGenerator = blockStateModelGenerator;
        this.blockStateOutput = blockStateModelGenerator.blockStateOutput;

        blockStateModelGenerator.createHorizontallyRotatedBlock(VivatechBlocks.GENERATOR, TexturedModelProviders.ADEQUATE_CUBE_COLUMN_HORIZONTAL);
        blockStateModelGenerator.createHorizontallyRotatedBlock(VivatechBlocks.GEARBOX, TexturedModelProviders.ADEQUATE_CUBE_COLUMN_HORIZONTAL);
        blockStateModelGenerator.createHorizontallyRotatedBlock(VivatechBlocks.WIND_TURBINE, createDefault(TextureMapping::particle, ModelTemplates.PARTICLE_ONLY));

        blockStateModelGenerator.createTrivialBlock(VivatechBlocks.HEATER, TexturedModel.CUBE_TOP_BOTTOM);

        blockStateModelGenerator.createTrivialBlock(VivatechBlocks.STATIC_COMPENSATOR, TexturedModel.CUBE_TOP_BOTTOM);
        blockStateModelGenerator.createHorizontallyRotatedBlock(VivatechBlocks.CONTACTOR, TexturedModelProviders.ADEQUATE_CUBE_COLUMN_HORIZONTAL);

        registerWirePost();
    }

    private void registerWirePost() {
        blockStateOutput.accept(
                MultiVariantGenerator
                        .dispatch(VivatechBlocks.WIRE_POST, plainVariant(ModelLocationUtils.getModelLocation(VivatechBlocks.WIRE_POST)))
                        .with(BlockModelGenerators.ROTATION_FACING)
        );
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(VivatechItems.WIND_TURBINE, ModelTemplates.FLAT_ITEM);

        itemModelGenerator.generateFlatItem(VivatechItems.MORTAR_AND_PESTLE, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(VivatechItems.COPPER_WIRE, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(VivatechItems.SCREWDRIVER, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(VivatechItems.PLIERS, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(VivatechItems.PROBE, ModelTemplates.FLAT_ITEM);
    }
}

package falseresync.vivatech.datagen;

import falseresync.vivatech.client.rendering.item.FocusPlatingModelProperty;
import falseresync.vivatech.common.block.VivatechBlocks;
import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.item.VivatechItems;
import falseresync.vivatech.common.item.focus.FocusItem;
import falseresync.vivatech.common.item.focus.FocusPlating;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.*;
import net.minecraft.client.renderer.item.RangeSelectItemModel;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;

import static falseresync.vivatech.common.Vivatech.vtId;
import static net.minecraft.client.data.models.BlockModelGenerators.plainVariant;
import static net.minecraft.client.data.models.model.TexturedModel.createDefault;

public class VivatechModelProvider extends FabricModelProvider {
    public static class VivatechModels {
        public static final ModelTemplate ADEQUATE_CUBE_COLUMN_HORIZONTAL = new ModelTemplate(Optional.of(vtId("block/cube_column_horizontal")), Optional.empty(), TextureSlot.SIDE, TextureSlot.END);
    }

    public static class VivatechTexturedModels {
        public static final TexturedModel.Provider ADEQUATE_CUBE_COLUMN_HORIZONTAL = createDefault(TextureMapping::column, VivatechModels.ADEQUATE_CUBE_COLUMN_HORIZONTAL);
    }

    private BlockModelGenerators blockStateModelGenerator;
    private Consumer<BlockModelDefinitionGenerator> blockStateOutput;

    public VivatechModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        this.blockStateModelGenerator = blockStateModelGenerator;
        this.blockStateOutput = blockStateModelGenerator.blockStateOutput;

        blockStateModelGenerator.createHorizontallyRotatedBlock(VivatechBlocks.GENERATOR, VivatechTexturedModels.ADEQUATE_CUBE_COLUMN_HORIZONTAL);
        blockStateModelGenerator.createHorizontallyRotatedBlock(VivatechBlocks.GEARBOX, VivatechTexturedModels.ADEQUATE_CUBE_COLUMN_HORIZONTAL);
        blockStateModelGenerator.createHorizontallyRotatedBlock(VivatechBlocks.WIND_TURBINE, createDefault(TextureMapping::particle, ModelTemplates.PARTICLE_ONLY));

        blockStateModelGenerator.createTrivialBlock(VivatechBlocks.HEATER, TexturedModel.CUBE_TOP_BOTTOM);
        blockStateModelGenerator.createTrivialBlock(VivatechBlocks.CHARGER, TexturedModel.CUBE_TOP_BOTTOM);

        blockStateModelGenerator.createTrivialBlock(VivatechBlocks.STATIC_COMPENSATOR, TexturedModel.CUBE_TOP_BOTTOM);
        blockStateModelGenerator.createHorizontallyRotatedBlock(VivatechBlocks.CONTACTOR, VivatechTexturedModels.ADEQUATE_CUBE_COLUMN_HORIZONTAL);

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
        itemModelGenerator.generateFlatItem(VivatechItems.WIRE, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(VivatechItems.SCREWDRIVER, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(VivatechItems.PLIERS, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(VivatechItems.PROBE, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(VivatechItems.INSPECTOR_GOGGLES, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(VivatechItems.FOCUSES_POUCH, ModelTemplates.FLAT_ITEM);

        itemModelGenerator.generateFlatItem(VivatechItems.GADGET, ModelTemplates.FLAT_HANDHELD_ROD_ITEM);

        registerFocus(VivatechItems.STARSHOOTER_FOCUS, itemModelGenerator);
        registerFocus(VivatechItems.LIGHTNING_FOCUS, itemModelGenerator);
        registerFocus(VivatechItems.COMET_WARP_FOCUS, itemModelGenerator);
        registerFocus(VivatechItems.ENERGY_VEIL_FOCUS, itemModelGenerator);
    }

//    private JsonObject createFocusJson(Identifier id, Map<TextureSlot, Identifier> textures) {
//        var model = ModelTemplates.TWO_LAYERED_ITEM.createBaseTemplate(id, textures);
//        var overrides = new JsonArray();
//
//        for (var plating : FocusPlating.values()) {
//            var override = new JsonObject();
//            var predicate = new JsonObject();
//            predicate.addProperty("vivatech:focus_plating", plating.index);
//            override.add("predicate", predicate);
//            override.addProperty("model", DatagenUtil.suffixPlating(id, plating).toString());
//            overrides.add(override);
//        }
//
//        model.add("overrides", overrides);
//        return model;
//    }

    private void registerFocus(FocusItem focus, ItemModelGenerators generator) {
        // TODO
        Identifier textureId = TextureMapping.getItemTexture(focus);

        Identifier modelId = ModelTemplates.FLAT_ITEM.create(
                ModelLocationUtils.getModelLocation(focus),
                TextureMapping.layer0(textureId),
                generator.modelOutput
        );

        var platedVariants = new ArrayList<RangeSelectItemModel.Entry>();
        for (var plating : FocusPlating.values()) {
            var platedModelId = ModelTemplates.TWO_LAYERED_ITEM.create(
                    DatagenUtil.suffixPlating(modelId, plating),
                    TextureMapping.layered(textureId, DatagenUtil.suffixPlating(vtId("item/focus"), plating)),
                    generator.modelOutput
            );
            platedVariants.add(ItemModelUtils.override(ItemModelUtils.plainModel(platedModelId), plating.index));
        }

        generator.itemModelOutput.accept(
                focus,
                ItemModelUtils.conditional(
                        ItemModelUtils.hasComponent(VivatechComponents.FOCUS_PLATING),
                        ItemModelUtils.rangeSelect(new FocusPlatingModelProperty(), platedVariants),
                        ItemModelUtils.plainModel(modelId)
                )
        );
    }
}

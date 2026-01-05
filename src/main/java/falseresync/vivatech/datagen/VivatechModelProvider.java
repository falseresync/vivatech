package falseresync.vivatech.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import falseresync.vivatech.common.block.VivatechBlocks;
import falseresync.vivatech.common.item.VivatechItems;
import falseresync.vivatech.common.item.focus.FocusItem;
import falseresync.vivatech.common.item.focus.FocusPlating;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.model.*;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.Optional;

import static falseresync.vivatech.common.Vivatech.vtId;
import static net.minecraft.client.data.models.model.TexturedModel.createDefault;

public class VivatechModelProvider extends FabricModelProvider {
    public static class VivatechModels {
        public static final ModelTemplate ADEQUATE_CUBE_COLUMN_HORIZONTAL = new ModelTemplate(Optional.of(vtId("block/cube_column_horizontal")), Optional.empty(), TextureSlot.SIDE, TextureSlot.END);
    }

    public static class VivatechTexturedModels {
        public static final TexturedModel.Provider ADEQUATE_CUBE_COLUMN_HORIZONTAL = createDefault(TextureMapping::column, VivatechModels.ADEQUATE_CUBE_COLUMN_HORIZONTAL);
    }

    private BlockModelGenerators blockStateModelGenerator;

    public VivatechModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        this.blockStateModelGenerator = blockStateModelGenerator;

        blockStateModelGenerator.createHorizontallyRotatedBlock(VivatechBlocks.GENERATOR, VivatechTexturedModels.ADEQUATE_CUBE_COLUMN_HORIZONTAL);
        blockStateModelGenerator.createHorizontallyRotatedBlock(VivatechBlocks.GEARBOX, VivatechTexturedModels.ADEQUATE_CUBE_COLUMN_HORIZONTAL);
        registerWindTurbine();

        blockStateModelGenerator.createTrivialBlock(VivatechBlocks.HEATER, TexturedModel.CUBE_TOP_BOTTOM);
        blockStateModelGenerator.createTrivialBlock(VivatechBlocks.CHARGER, TexturedModel.CUBE_TOP_BOTTOM);

        blockStateModelGenerator.createTrivialBlock(VivatechBlocks.STATIC_COMPENSATOR, TexturedModel.CUBE_TOP_BOTTOM);
        blockStateModelGenerator.createHorizontallyRotatedBlock(VivatechBlocks.CONTACTOR, VivatechTexturedModels.ADEQUATE_CUBE_COLUMN_HORIZONTAL);

        registerWirePost();
    }

    private void registerWindTurbine() {
        var windTurbineModelId = TexturedModel.PARTICLE_ONLY.create(VivatechBlocks.WIND_TURBINE, blockStateModelGenerator.modelOutput);
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator
                        .multiVariant(
                                VivatechBlocks.WIND_TURBINE,
                                Variant.variant().with(VariantProperties.MODEL, windTurbineModelId)
                        ).with(BlockModelGenerators.createHorizontalFacingDispatch())
        );
        blockStateModelGenerator.skipAutoItemBlock(VivatechBlocks.WIND_TURBINE);
    }

    private void registerWirePost() {
        blockStateModelGenerator.blockStateOutput.accept(
                MultiVariantGenerator
                        .dispatch(
                                VivatechBlocks.WIRE_POST,
                                Variant.variant().with(VariantProperties.MODEL, ModelLocationUtils.getModelLocation(VivatechBlocks.WIRE_POST))
                        ).with(BlockModelGenerators.createFacingDispatch())
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

    private JsonObject createFocusJson(Identifier id, Map<TextureSlot, Identifier> textures) {
        var model = ModelTemplates.TWO_LAYERED_ITEM.createBaseTemplate(id, textures);
        var overrides = new JsonArray();

        for (var plating : FocusPlating.values()) {
            var override = new JsonObject();
            var predicate = new JsonObject();
            predicate.addProperty("vivatech:focus_plating", plating.index);
            override.add("predicate", predicate);
            override.addProperty("model", falseresync.vivatech.datagen.DatagenUtil.suffixPlating(id, plating).toString());
            overrides.add(override);
        }

        model.add("overrides", overrides);
        return model;
    }

    private void registerFocus(FocusItem focus, ItemModelGenerators generator) {
        Identifier modelId = ModelLocationUtils.getModelLocation(focus);
        Identifier textureId = TextureMapping.getItemTexture(focus);
        ModelTemplates.FLAT_ITEM.create(modelId, TextureMapping.layer0(textureId), this::createFocusJson,generator.modelOutput, );

        for (var plating : FocusPlating.values()) {
            ModelTemplates.TWO_LAYERED_ITEM.create(
                    falseresync.vivatech.datagen.DatagenUtil.suffixPlating(modelId, plating),
                    TextureMapping.layered(textureId, DatagenUtil.suffixPlating(vtId("item/focus"), plating)),
                    generator.output);
        }
    }
}

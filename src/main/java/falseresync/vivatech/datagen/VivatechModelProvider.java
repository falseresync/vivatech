package falseresync.vivatech.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import falseresync.vivatech.common.block.VivatechBlocks;
import falseresync.vivatech.common.item.VivatechItems;
import falseresync.vivatech.common.item.focus.FocusItem;
import falseresync.vivatech.common.item.focus.FocusPlating;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.client.*;
import net.minecraft.util.Identifier;

import java.util.Map;

import static falseresync.vivatech.common.Vivatech.vtId;

public class VivatechModelProvider extends FabricModelProvider {
    private BlockStateModelGenerator blockStateModelGenerator;

    public VivatechModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        this.blockStateModelGenerator = blockStateModelGenerator;

        blockStateModelGenerator.registerNorthDefaultHorizontalRotated(VivatechBlocks.GENERATOR, TexturedModel.CUBE_COLUMN_HORIZONTAL);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotated(VivatechBlocks.GEARBOX, TexturedModel.CUBE_COLUMN_HORIZONTAL);
        registerWindmill();
        blockStateModelGenerator.registerSingleton(VivatechBlocks.HEATER, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(VivatechBlocks.STATIC_COMPENSATOR, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(VivatechBlocks.CHARGER, TexturedModel.CUBE_BOTTOM_TOP);
        registerWirePost();
    }

    private void registerWindmill() {
        var windmillModelId = TexturedModel.PARTICLE.upload(VivatechBlocks.WINDMILL, blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier
                        .create(
                                VivatechBlocks.WINDMILL,
                                BlockStateVariant.create().put(VariantSettings.MODEL, windmillModelId)
                        ).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates())
        );
        blockStateModelGenerator.excludeFromSimpleItemModelGeneration(VivatechBlocks.WINDMILL);
    }

    private void registerWirePost() {
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier
                        .create(
                                VivatechBlocks.WIRE_POST,
                                BlockStateVariant.create().put(VariantSettings.MODEL, ModelIds.getBlockModelId(VivatechBlocks.WIRE_POST))
                        ).coordinate(BlockStateModelGenerator.createNorthDefaultRotationStates())
        );
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(VivatechItems.WINDMILL, Models.GENERATED);

        itemModelGenerator.register(VivatechItems.MORTAR_AND_PESTLE, Models.GENERATED);
        itemModelGenerator.register(VivatechItems.CONNECTOR, Models.GENERATED);
        itemModelGenerator.register(VivatechItems.PLIERS, Models.GENERATED);
        itemModelGenerator.register(VivatechItems.INSPECTOR_GOGGLES, Models.GENERATED);

        itemModelGenerator.register(VivatechItems.GADGET, Models.HANDHELD_ROD);

        registerFocus(VivatechItems.STARSHOOTER_FOCUS, itemModelGenerator);
        registerFocus(VivatechItems.LIGHTNING_FOCUS, itemModelGenerator);
        registerFocus(VivatechItems.COMET_WARP_FOCUS, itemModelGenerator);
        registerFocus(VivatechItems.ENERGY_VEIL_FOCUS, itemModelGenerator);
    }

    private JsonObject createFocusJson(Identifier id, Map<TextureKey, Identifier> textures) {
        var model = Models.GENERATED_TWO_LAYERS.createJson(id, textures);
        var overrides = new JsonArray();

        for (var plating : FocusPlating.values()) {
            var override = new JsonObject();
            var predicate = new JsonObject();
            predicate.addProperty("vivatech:focus_plating", plating.index);
            override.add("predicate", predicate);
            override.addProperty("model", DatagenUtil.suffixPlating(id, plating).toString());
            overrides.add(override);
        }

        model.add("overrides", overrides);
        return model;
    }

    private void registerFocus(FocusItem focus, ItemModelGenerator generator) {
        Identifier modelId = ModelIds.getItemModelId(focus);
        Identifier textureId = TextureMap.getId(focus);
        Models.GENERATED.upload(modelId, TextureMap.layer0(textureId), generator.writer, this::createFocusJson);

        for (var plating : FocusPlating.values()) {
            Models.GENERATED_TWO_LAYERS.upload(
                    DatagenUtil.suffixPlating(modelId, plating),
                    TextureMap.layered(textureId, DatagenUtil.suffixPlating(vtId("item/focus"), plating)),
                    generator.writer);
        }
    }
}

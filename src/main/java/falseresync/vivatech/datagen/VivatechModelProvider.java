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
import java.util.Optional;

import static falseresync.vivatech.common.Vivatech.vtId;
import static net.minecraft.data.client.TexturedModel.makeFactory;

public class VivatechModelProvider extends FabricModelProvider {
    public static class VivatechModels {
        public static final Model ADEQUATE_CUBE_COLUMN_HORIZONTAL = new Model(Optional.of(vtId("block/cube_column_horizontal")), Optional.empty(), TextureKey.SIDE, TextureKey.END);
    }

    public static class VivatechTexturedModels {
        public static final TexturedModel.Factory ADEQUATE_CUBE_COLUMN_HORIZONTAL = makeFactory(TextureMap::sideEnd, VivatechModels.ADEQUATE_CUBE_COLUMN_HORIZONTAL);
    }

    private BlockStateModelGenerator blockStateModelGenerator;

    public VivatechModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        this.blockStateModelGenerator = blockStateModelGenerator;

        blockStateModelGenerator.registerNorthDefaultHorizontalRotated(VivatechBlocks.GENERATOR, VivatechTexturedModels.ADEQUATE_CUBE_COLUMN_HORIZONTAL);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotated(VivatechBlocks.GEARBOX, VivatechTexturedModels.ADEQUATE_CUBE_COLUMN_HORIZONTAL);
        registerWindTurbine();

        blockStateModelGenerator.registerSingleton(VivatechBlocks.HEATER, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerSingleton(VivatechBlocks.CHARGER, TexturedModel.CUBE_BOTTOM_TOP);

        blockStateModelGenerator.registerSingleton(VivatechBlocks.STATIC_COMPENSATOR, TexturedModel.CUBE_BOTTOM_TOP);
        blockStateModelGenerator.registerNorthDefaultHorizontalRotated(VivatechBlocks.CONTACTOR, VivatechTexturedModels.ADEQUATE_CUBE_COLUMN_HORIZONTAL);

        registerWirePost();
    }

    private void registerWindTurbine() {
        var windTurbineModelId = TexturedModel.PARTICLE.upload(VivatechBlocks.WIND_TURBINE, blockStateModelGenerator.modelCollector);
        blockStateModelGenerator.blockStateCollector.accept(
                VariantsBlockStateSupplier
                        .create(
                                VivatechBlocks.WIND_TURBINE,
                                BlockStateVariant.create().put(VariantSettings.MODEL, windTurbineModelId)
                        ).coordinate(BlockStateModelGenerator.createNorthDefaultHorizontalRotationStates())
        );
        blockStateModelGenerator.excludeFromSimpleItemModelGeneration(VivatechBlocks.WIND_TURBINE);
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
        itemModelGenerator.register(VivatechItems.WIND_TURBINE, Models.GENERATED);

        itemModelGenerator.register(VivatechItems.MORTAR_AND_PESTLE, Models.GENERATED);
        itemModelGenerator.register(VivatechItems.WIRE, Models.GENERATED);
        itemModelGenerator.register(VivatechItems.SCREWDRIVER, Models.GENERATED);
        itemModelGenerator.register(VivatechItems.PLIERS, Models.GENERATED);
        itemModelGenerator.register(VivatechItems.PROBE, Models.GENERATED);
        itemModelGenerator.register(VivatechItems.INSPECTOR_GOGGLES, Models.GENERATED);
        itemModelGenerator.register(VivatechItems.FOCUSES_POUCH, Models.GENERATED);

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

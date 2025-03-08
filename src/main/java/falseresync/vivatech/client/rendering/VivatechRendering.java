package falseresync.vivatech.client.rendering;

import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import falseresync.vivatech.client.rendering.block.ChargerBlockEntityRenderer;
import falseresync.vivatech.client.rendering.block.WindmillBlockEntityRenderer;
import falseresync.vivatech.client.rendering.entity.EnergyVeilFeatureRenderer;
import falseresync.vivatech.client.rendering.entity.EnergyVeilModel;
import falseresync.vivatech.client.rendering.entity.StarProjectileRenderer;
import falseresync.vivatech.client.rendering.trinket.InspectorGogglesRenderer;
import falseresync.vivatech.client.rendering.world.CometWarpBeaconRenderer;
import falseresync.vivatech.client.rendering.world.WireRenderer;
import falseresync.vivatech.common.block.VivatechBlocks;
import falseresync.vivatech.common.blockentity.VivatechBlockEntities;
import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.entity.VivatechEntities;
import falseresync.vivatech.common.item.VivatechItems;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.EmptyEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;

import static falseresync.vivatech.common.Vivatech.vtId;

public class VivatechRendering {
    public static void init() {
        EntityModelLayerRegistry.registerModelLayer(WindmillBlockEntityRenderer.LAYER, WindmillBlockEntityRenderer::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(EnergyVeilFeatureRenderer.LAYER, EnergyVeilModel::getTexturedModelData);

        EntityRendererRegistry.register(VivatechEntities.STAR_PROJECTILE, StarProjectileRenderer::new);
        EntityRendererRegistry.register(VivatechEntities.ENERGY_VEIL, EmptyEntityRenderer::new);

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityRenderer instanceof PlayerEntityRenderer) {
                registrationHelper.register(new EnergyVeilFeatureRenderer<>((PlayerEntityRenderer) entityRenderer, context.getModelLoader()));
            }
        });

        BlockEntityRendererFactories.register(VivatechBlockEntities.WINDMILL, WindmillBlockEntityRenderer::new);
        BlockEntityRendererFactories.register(VivatechBlockEntities.CHARGER, ChargerBlockEntityRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                VivatechBlocks.WIRE_POST,
                VivatechBlocks.WINDMILL
        );

        ModelPredicateProviderRegistry.GLOBAL.put(vtId("focus_plating"), (stack, world, entity, seed) -> stack.getOrDefault(VivatechComponents.FOCUS_PLATING, -1));

        TrinketRendererRegistry.registerRenderer(VivatechItems.INSPECTOR_GOGGLES, new InspectorGogglesRenderer());

        WorldRenderEvents.AFTER_ENTITIES.register(new CometWarpBeaconRenderer());
        WorldRenderEvents.AFTER_ENTITIES.register(new WireRenderer());
    }
}

package falseresync.vivatech.client.rendering;

import dev.emi.trinkets.api.client.TrinketRendererRegistry;
import falseresync.vivatech.client.rendering.block.ChargerBlockEntityRenderer;
import falseresync.vivatech.client.rendering.block.WindTurbineBlockEntityRenderer;
import falseresync.vivatech.client.rendering.entity.EnergyVeilFeatureRenderer;
import falseresync.vivatech.client.rendering.entity.EnergyVeilModel;
import falseresync.vivatech.client.rendering.entity.StarProjectileRenderer;
import falseresync.vivatech.client.rendering.item.FocusPlatingModelProperty;
import falseresync.vivatech.client.rendering.trinket.InspectorGogglesRenderer;
import falseresync.vivatech.client.rendering.world.CometWarpBeaconRenderer;
import falseresync.vivatech.client.rendering.world.WireRenderer;
import falseresync.vivatech.common.block.VivatechBlocks;
import falseresync.vivatech.common.blockentity.VivatechBlockEntities;
import falseresync.vivatech.common.entity.VivatechEntities;
import falseresync.vivatech.common.item.VivatechItems;
import net.fabricmc.fabric.api.client.rendering.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.NoopRenderer;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;

import static falseresync.vivatech.common.Vivatech.vtId;

public class VivatechRendering {
    public static void init() {
        EntityModelLayerRegistry.registerModelLayer(WindTurbineBlockEntityRenderer.LAYER, WindTurbineBlockEntityRenderer::getTexturedModelData);
        EntityModelLayerRegistry.registerModelLayer(EnergyVeilFeatureRenderer.LAYER, EnergyVeilModel::getTexturedModelData);

        EntityRenderers.register(VivatechEntities.STAR_PROJECTILE, StarProjectileRenderer::new);
        EntityRenderers.register(VivatechEntities.ENERGY_VEIL, NoopRenderer::new);

        BlockEntityRenderers.register(VivatechBlockEntities.WIND_TURBINE, WindTurbineBlockEntityRenderer::new);
        BlockEntityRenderers.register(VivatechBlockEntities.CHARGER, ChargerBlockEntityRenderer::new);

        BlockRenderLayerMap.putBlocks(ChunkSectionLayer.CUTOUT,
                VivatechBlocks.WIRE_POST,
                VivatechBlocks.WIND_TURBINE
        );

        RangeSelectItemModelProperties.ID_MAPPER.put(vtId("focus_plating"), FocusPlatingModelProperty.MAP_CODEC);

        TrinketRendererRegistry.registerRenderer(VivatechItems.INSPECTOR_GOGGLES, new InspectorGogglesRenderer());

        WorldRenderEvents.AFTER_ENTITIES.register(new CometWarpBeaconRenderer());
        WorldRenderEvents.AFTER_ENTITIES.register(new WireRenderer());
    }
}

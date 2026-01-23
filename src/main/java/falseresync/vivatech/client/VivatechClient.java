package falseresync.vivatech.client;

import com.google.common.base.Preconditions;
import falseresync.vivatech.client.rendering.block.WindTurbineBlockEntityRenderer;
import falseresync.vivatech.client.rendering.world.WireRenderer;
import falseresync.vivatech.client.wire.WiresManager;
import falseresync.vivatech.client.wire.WireRenderingRegistry;
import falseresync.vivatech.network.VivatechClientNetworking;
import falseresync.vivatech.world.block.VivatechBlocks;
import falseresync.vivatech.world.blockentity.VivatechBlockEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ChunkSectionLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import org.jspecify.annotations.Nullable;

public class VivatechClient implements ClientModInitializer {
	@Nullable
	private static WiresManager wiresManager;

	@Override
	public void onInitializeClient() {
		VivatechClientNetworking.init();
		WireRenderingRegistry.init();

		ClientLifecycleEvents.CLIENT_STARTED.register(minecraft -> {
			wiresManager = new WiresManager(minecraft);
		});


		ModelLayerRegistry.registerModelLayer(WindTurbineBlockEntityRenderer.LAYER, WindTurbineBlockEntityRenderer::getTexturedModelData);

		BlockEntityRenderers.register(VivatechBlockEntities.WIND_TURBINE, WindTurbineBlockEntityRenderer::new);

		ChunkSectionLayerMap.putBlocks(ChunkSectionLayer.CUTOUT,
				VivatechBlocks.WIRE_POST,
				VivatechBlocks.WIND_TURBINE
		);

		LevelRenderEvents.AFTER_ENTITIES.register(new WireRenderer());
	}

	public static WiresManager getWiresManager() {
		Preconditions.checkState(wiresManager != null, "Trying to access WireManager before it's instantiated");
		return wiresManager;
	}
}
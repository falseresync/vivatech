package falseresync.vivatech.client;

import falseresync.vivatech.client.render.WireRenderer;
import falseresync.vivatech.common.block.VtBlocks;
import falseresync.vivatech.network.VtClientReceivers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.RenderLayer;

public class VivatechClient implements ClientModInitializer {
    private static ClientWireManager clientWireManager;

    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), VtBlocks.WIRE_POST);

        VtClientReceivers.registerAll();

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            clientWireManager = new ClientWireManager(client);
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            clientWireManager.tick();
        });

        ClientChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
            if (clientWireManager != null) {
                clientWireManager.queueUnsyncedChunk(chunk.getPos());
            }
        });

        WorldRenderEvents.AFTER_ENTITIES.register(new WireRenderer());
    }

    public static ClientWireManager getClientWireManager() {
        return clientWireManager;
    }
}

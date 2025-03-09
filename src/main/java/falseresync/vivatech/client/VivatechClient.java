package falseresync.vivatech.client;

import falseresync.vivatech.client.gui.VivatechGui;
import falseresync.vivatech.client.hud.VivatechHud;
import falseresync.vivatech.client.particle.VivatechParticleFactories;
import falseresync.vivatech.client.rendering.VivatechRendering;
import falseresync.vivatech.common.config.TranslatableEnum;
import falseresync.vivatech.common.config.TranslatableEnumGuiProvider;
import falseresync.vivatech.common.config.VivatechConfig;
import falseresync.vivatech.network.VivatechClientReceivers;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class VivatechClient implements ClientModInitializer {
    private static ClientWireManager clientWireManager;
    private static VivatechHud hud;
    private static ToolManager toolManager;

    @Override
    public void onInitializeClient() {
        AutoConfig.getGuiRegistry(VivatechConfig.class).registerPredicateProvider(
                new TranslatableEnumGuiProvider<>(),
                field -> field.getType().isEnum() && field.isAnnotationPresent(TranslatableEnum.class)
        );
        
        VivatechRendering.init();
        VivatechParticleFactories.init();
        VivatechGui.init();
        VivatechKeybindings.init();
        VivatechClientReceivers.registerAll();
        ClientPlayerInventoryEvents.init();

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            clientWireManager = new ClientWireManager(client);
            hud = new VivatechHud(client);
            toolManager = new ToolManager();
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            clientWireManager.tick();
        });

        ClientChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
            if (clientWireManager != null) {
                clientWireManager.queueUnsyncedChunk(chunk.getPos());
            }
        });
    }

    public static ClientWireManager getClientWireManager() {
        return clientWireManager;
    }
    
    public static VivatechHud getHud() {
        return hud;
    }

    public static ToolManager getToolManager() {
        return toolManager;
    }
}

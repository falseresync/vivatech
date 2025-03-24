package falseresync.vivatech.client;

import falseresync.lib.logging.BetterLogger;
import falseresync.vivatech.client.gui.VivatechGui;
import falseresync.vivatech.client.hud.VivatechHud;
import falseresync.vivatech.client.particle.VivatechParticleFactories;
import falseresync.vivatech.client.rendering.VivatechRendering;
import falseresync.vivatech.client.wire.ClientWireManager;
import falseresync.vivatech.client.wire.WireRenderingRegistry;
import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.config.TranslatableEnum;
import falseresync.vivatech.common.config.TranslatableEnumGuiProvider;
import falseresync.vivatech.common.config.VivatechConfig;
import falseresync.vivatech.compat.anshar.AnsharCompatClient;
import falseresync.vivatech.network.VivatechClientReceivers;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.LoggerFactory;

public class VivatechClient implements ClientModInitializer {
    public static final BetterLogger LOGGER = new BetterLogger(LoggerFactory.getLogger(Vivatech.MOD_ID), "Vivatech / Client");
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
        WireRenderingRegistry.registerAll();
        VivatechClientReceivers.registerAll();
        ClientPlayerInventoryEvents.init();

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            clientWireManager = new ClientWireManager(client);
            hud = new VivatechHud(client);
            toolManager = new ToolManager();

            if (FabricLoader.getInstance().isModLoaded("anshar")) {
                AnsharCompatClient.init(client);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            clientWireManager.tick();
        });

        LOGGER.info("Initialized");
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

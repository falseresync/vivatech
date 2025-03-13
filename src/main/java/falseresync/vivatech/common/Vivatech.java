package falseresync.vivatech.common;

import falseresync.lib.registry.AutoRegistry;
import falseresync.vivatech.common.block.VivatechBlocks;
import falseresync.vivatech.common.blockentity.VivatechBlockEntities;
import falseresync.vivatech.common.config.VivatechConfig;
import falseresync.vivatech.common.data.VivatechAttachments;
import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.entity.VivatechEntities;
import falseresync.vivatech.common.item.VivatechItemGroups;
import falseresync.vivatech.common.item.VivatechItems;
import falseresync.vivatech.common.item.focus.TransmutationFocusBehavior;
import falseresync.vivatech.common.power.Grid;
import falseresync.vivatech.common.power.PowerSystem;
import falseresync.vivatech.common.power.ServerGridsLoader;
import falseresync.vivatech.common.power.WireType;
import falseresync.vivatech.network.VivatechNetworking;
import falseresync.vivatech.network.VivatechServerReceivers;
import falseresync.vivatech.network.report.Reports;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vivatech implements ModInitializer {
    public static final String MOD_ID = "vivatech";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    private static ChargeManager chargeManager;
    private static VivatechConfig config;
    private static ServerGridsLoader serverGridsLoader;

    public static ServerGridsLoader getServerGridsLoader() {
        return serverGridsLoader;
    }

    public static ChargeManager getChargeManager() {
        return chargeManager;
    }

    public static VivatechConfig getConfig() {
        return config;
    }

    public static Identifier vtId(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        config = AutoConfig.register(VivatechConfig.class, JanksonConfigSerializer::new).getConfig();

        VivatechBlocks.registerAll();
        VivatechItems.registerAll();
        new AutoRegistry(MOD_ID, LOGGER)
                .link(Registries.BLOCK_ENTITY_TYPE, VivatechBlockEntities.class)
                .link(Registries.ITEM_GROUP, VivatechItemGroups.class)
                .link(Registries.DATA_COMPONENT_TYPE, VivatechComponents.class)
                .link(Registries.ENTITY_TYPE, VivatechEntities.class)
                .link(Registries.PARTICLE_TYPE, VivatechParticleTypes.class)
                .link(Reports.REGISTRY, Reports.class)
                .link(WireType.REGISTRY, WireType.class);
        VivatechAttachments.init();
        VivatechSounds.init();
        PowerSystem.registerAll();
        VivatechNetworking.registerAll();
        VivatechServerReceivers.registerAll();

        chargeManager = new ChargeManager();

        TransmutationFocusBehavior.register();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            serverGridsLoader = new ServerGridsLoader(server);
        });

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            serverGridsLoader.close();
        });

        ServerLifecycleEvents.AFTER_SAVE.register((server, flush, force) -> {
            serverGridsLoader.save();
        });

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            serverGridsLoader.tick(world);
        });
    }
}
package falseresync.vivatech.common;

import falseresync.lib.logging.BetterLogger;
import falseresync.lib.registry.AutoRegistry;
import falseresync.vivatech.common.block.VivatechBlocks;
import falseresync.vivatech.common.blockentity.VivatechBlockEntities;
import falseresync.vivatech.common.config.VivatechConfig;
import falseresync.vivatech.common.data.VivatechAttachments;
import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.entity.VivatechEntities;
import falseresync.vivatech.common.item.VivatechItemGroups;
import falseresync.vivatech.common.item.VivatechItemTags;
import falseresync.vivatech.common.item.VivatechItems;
import falseresync.vivatech.common.item.focus.CometWarpFocusItem;
import falseresync.vivatech.common.item.focus.TransmutationFocusBehavior;
import falseresync.vivatech.common.power.PowerSystem;
import falseresync.vivatech.common.power.wire.WireType;
import falseresync.vivatech.compat.anshar.AnsharCompatImpl;
import falseresync.vivatech.network.VivatechNetworking;
import falseresync.vivatech.network.VivatechServerReceivers;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.LoggerFactory;

public class Vivatech implements ModInitializer {
    public static final String MOD_ID = "vivatech";
    public static final BetterLogger LOGGER = new BetterLogger(LoggerFactory.getLogger(MOD_ID), "Vivatech");
    private static ChargeManager chargeManager;
    private static VivatechConfig config;
    private static PowerSystem powerSystem;

    public static PowerSystem getPowerSystem() {
        return powerSystem;
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
        LOGGER.warn("This is an alpha version of the mod! It is very unstable and might break your world, especially when updating.");
        LOGGER.warn("Use at your own risk, but I will appreciate a bug report should anything happen");

        config = AutoConfig.register(VivatechConfig.class, JanksonConfigSerializer::new).getConfig();

        VivatechBlocks.registerAll();
        VivatechItems.registerAll();
        VivatechItemTags.init();
        new AutoRegistry(MOD_ID, LOGGER.getDelegate())
                .link(Registries.BLOCK_ENTITY_TYPE, VivatechBlockEntities.class)
                .link(Registries.ITEM_GROUP, VivatechItemGroups.class)
                .link(Registries.DATA_COMPONENT_TYPE, VivatechComponents.class)
                .link(Registries.ENTITY_TYPE, VivatechEntities.class)
                .link(Registries.PARTICLE_TYPE, VivatechParticleTypes.class)
                .link(WireType.REGISTRY, WireType.class);
        VivatechAttachments.init();
        VivatechSounds.init();
        PowerSystem.registerAll();
        VivatechNetworking.registerAll();
        VivatechServerReceivers.registerAll();

        chargeManager = new ChargeManager();

        TransmutationFocusBehavior.register();

        ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            powerSystem = new PowerSystem(server);
        });

        if (FabricLoader.getInstance().isModLoaded("anshar")) {
            CometWarpFocusItem.ansharCompat = new AnsharCompatImpl();
        }

        LOGGER.info("Initialized");
    }
}
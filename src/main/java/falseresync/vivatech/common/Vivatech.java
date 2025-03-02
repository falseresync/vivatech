package falseresync.vivatech.common;

import falseresync.vivatech.common.block.VtBlocks;
import falseresync.vivatech.common.blockentity.VtBlockEntities;
import falseresync.vivatech.common.data.VtComponents;
import falseresync.vivatech.common.item.VtItemGroups;
import falseresync.vivatech.common.item.VtItems;
import falseresync.lib.registry.AutoRegistry;
import falseresync.vivatech.common.power.PowerSystem;
import falseresync.vivatech.common.power.PowerSystemsManager;
import falseresync.vivatech.network.VivatechNetworking;
import falseresync.vivatech.network.VtServerReceivers;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.registry.Registries;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vivatech implements ModInitializer {
	public static final String MOD_ID = "vivatech";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	private static PowerSystemsManager powerSystemsManager;

	@Override
	public void onInitialize() {
		VtBlocks.registerAll();
		VtItems.registerAll();
		new AutoRegistry(MOD_ID, LOGGER)
				.link(Registries.BLOCK_ENTITY_TYPE, VtBlockEntities.class)
				.link(Registries.ITEM_GROUP, VtItemGroups.class)
				.link(Registries.DATA_COMPONENT_TYPE, VtComponents.class);
		PowerSystemsManager.registerAll();

		VivatechNetworking.registerAll();
		VtServerReceivers.registerAll();

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			powerSystemsManager = new PowerSystemsManager(server);
		});

		ServerLifecycleEvents.AFTER_SAVE.register((server, flush, force) -> {
			powerSystemsManager.save();
		});

		ServerTickEvents.END_WORLD_TICK.register(world -> {
			powerSystemsManager.getAll(world).forEach(PowerSystem::tick);
			powerSystemsManager.sendUnsyncedWires();
			powerSystemsManager.sendRequestedWires();
		});
	}

	public static PowerSystemsManager getPowerSystemsManager() {
		return powerSystemsManager;
	}

	public static Identifier vtId(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
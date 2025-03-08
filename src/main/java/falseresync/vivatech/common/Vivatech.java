package falseresync.vivatech.common;

import falseresync.vivatech.common.block.VtBlocks;
import falseresync.vivatech.common.blockentity.VtBlockEntities;
import falseresync.vivatech.common.data.VtComponents;
import falseresync.vivatech.common.item.VtItemGroups;
import falseresync.vivatech.common.item.VtItems;
import falseresync.lib.registry.AutoRegistry;
import falseresync.vivatech.common.power.Grid;
import falseresync.vivatech.common.power.PowerSystem;
import falseresync.vivatech.common.power.ServerGridsLoader;
import falseresync.vivatech.common.power.WireType;
import falseresync.vivatech.network.VivatechNetworking;
import falseresync.vivatech.network.VtServerReceivers;
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
	private static ServerGridsLoader serverGridsLoader;

	@Override
	public void onInitialize() {
		VtBlocks.registerAll();
		VtItems.registerAll();
		new AutoRegistry(MOD_ID, LOGGER)
				.link(Registries.BLOCK_ENTITY_TYPE, VtBlockEntities.class)
				.link(Registries.ITEM_GROUP, VtItemGroups.class)
				.link(Registries.DATA_COMPONENT_TYPE, VtComponents.class)
				.link(WireType.REGISTRY, WireType.class);
		PowerSystem.registerAll();

		VivatechNetworking.registerAll();
		VtServerReceivers.registerAll();

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			serverGridsLoader = new ServerGridsLoader(server);
		});

		ServerLifecycleEvents.AFTER_SAVE.register((server, flush, force) -> {
			serverGridsLoader.save();
		});

		ServerTickEvents.END_WORLD_TICK.register(world -> {
			serverGridsLoader.getGridsManager(world).getGrids().forEach(Grid::tick);
			serverGridsLoader.sendWires();
		});
	}

	public static ServerGridsLoader getServerGridsLoader() {
		return serverGridsLoader;
	}

	public static Identifier vtId(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
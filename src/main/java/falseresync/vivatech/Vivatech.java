package falseresync.vivatech;

import com.google.common.base.Preconditions;
import falseresync.vivatech.network.VivatechNetworking;
import falseresync.vivatech.world.block.VivatechBlocks;
import falseresync.vivatech.world.blockentity.VivatechBlockEntities;
import falseresync.vivatech.world.component.VivatechComponents;
import falseresync.vivatech.world.electricity.PowerSystem;
import falseresync.vivatech.world.electricity.PowerSystemsManager;
import falseresync.vivatech.world.item.VivatechCreativeTabs;
import falseresync.vivatech.world.item.VivatechItems;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jspecify.annotations.Nullable;


public class Vivatech implements ModInitializer {
	public static final String MOD_ID = "vivatech";

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Nullable
	private static PowerSystemsManager powerSystemsManager;

	@Override
	public void onInitialize() {
		VivatechBlocks.init();
		VivatechBlockEntities.init();
		VivatechComponents.init();
		VivatechItems.init();
		VivatechCreativeTabs.init();
		PowerSystemsManager.init();
		VivatechNetworking.init();

		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
			powerSystemsManager = new PowerSystemsManager(server);
		});
	}

	public static PowerSystemsManager getPowerSystemsManager() {
		Preconditions.checkState(powerSystemsManager != null, "Trying to access PowerSystemsManager before it's instantiated");
		return powerSystemsManager;
	}

	public static PowerSystem getPowerSystemFor(ResourceKey<Level> dimension) {
		return getPowerSystemsManager().getFor(dimension);
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
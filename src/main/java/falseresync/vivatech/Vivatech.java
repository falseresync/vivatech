package falseresync.vivatech;

import falseresync.vivatech.api.registry.AutoRegistry;
import falseresync.vivatech.block.VtBlocks;
import falseresync.vivatech.component.item.VtItemComponents;
import falseresync.vivatech.item.VtItemGroups;
import falseresync.vivatech.item.VtItems;
import net.fabricmc.api.ModInitializer;

import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vivatech implements ModInitializer {
	public static final String MOD_ID = "vivatech";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		new AutoRegistry(MOD_ID, LOGGER)
				.run(Registries.BLOCK, VtBlocks.class)
				.run(Registries.DATA_COMPONENT_TYPE, VtItemComponents.class)
				.run(Registries.ITEM, VtItems.class)
				.run(Registries.ITEM_GROUP, VtItemGroups.class);
	}

	public static Identifier vtId(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
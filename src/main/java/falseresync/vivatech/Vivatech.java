package falseresync.vivatech;

import falseresync.vivatech.block.VtBlocks;
import falseresync.vivatech.component.item.VtItemComponents;
import falseresync.vivatech.item.VtItemGroups;
import falseresync.vivatech.item.VtItems;
import net.fabricmc.api.ModInitializer;

import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vivatech implements ModInitializer {
	public static final String MOD_ID = "vivatech";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		VtBlocks.init();
		VtItems.init();
		VtItemGroups.init();
		VtItemComponents.init();
	}

	public static Identifier vtId(String path) {
		return Identifier.of(MOD_ID, path);
	}
}
package dev.falseresync.vivatech.common;

import dev.falseresync.vivatech.common.block.VivatechBlockGuis;
import dev.falseresync.vivatech.common.block.VivatechBlocks;
import dev.falseresync.vivatech.common.block.VivatechBlockEntities;
import dev.falseresync.vivatech.common.item.VivatechItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Vivatech implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("vivatech");

	@Override
	public void onInitialize() {
		VivatechBlocks.register();
		VivatechBlockEntities.register();
		VivatechItems.register();
		VivatechBlockGuis.register();
	}
}
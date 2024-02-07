package dev.falseresync.vivatech.client;

import dev.falseresync.vivatech.client.screen.VivatechScreens;
import net.fabricmc.api.ClientModInitializer;

public class VivatechClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		VivatechScreens.register();
	}
}
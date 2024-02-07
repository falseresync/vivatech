package dev.falseresync.vivatech.client.screen;

import dev.falseresync.vivatech.common.block.VivatechBlockGuis;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class VivatechScreens {
    public static void register() {
        HandledScreens.register(VivatechBlockGuis.STERLING_GENERATOR, SterlingGeneratorScreen::new);
    }
}

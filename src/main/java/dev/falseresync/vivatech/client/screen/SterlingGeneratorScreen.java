package dev.falseresync.vivatech.client.screen;

import dev.falseresync.vivatech.common.block.sterling_generator.SterlingGeneratorGui;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class SterlingGeneratorScreen extends CottonInventoryScreen<SterlingGeneratorGui> {
    public SterlingGeneratorScreen(SterlingGeneratorGui description, PlayerInventory playerInventory, Text title) {
        super(description, playerInventory, title);
    }
}

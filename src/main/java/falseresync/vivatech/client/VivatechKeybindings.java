package falseresync.vivatech.client;

import com.mojang.blaze3d.platform.InputConstants;
import falseresync.vivatech.client.VivatechClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public final class VivatechKeybindings {
    public static final KeyMapping TOOL_CONTROL = KeyBindingHelper.registerKeyBinding(new KeyMapping(
            "key.vivatech.tool_control",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT_ALT,
            "keyCategory.vivatech"
    ));

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                return;
            }

            while (TOOL_CONTROL.consumeClick()) {
                VivatechClient.getToolManager().onKeyPressed(client, client.player);
            }
        });
    }
}

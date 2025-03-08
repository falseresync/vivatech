package falseresync.vivatech.client.hud;

import falseresync.lib.client.BetterDrawContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class VivatechHud {
    private final MinecraftClient client;
    private final FocusPickerHudItem focusPicker;
    private final ChargeDisplayHudItem chargeDisplay;

    public VivatechHud(MinecraftClient client) {
        this.client = client;
        focusPicker = new FocusPickerHudItem(client, client.textRenderer);
        chargeDisplay = new ChargeDisplayHudItem(client, client.textRenderer);
        initEventListeners();
    }

    private void initEventListeners() {
        HudRenderCallback.EVENT.register((vanillaContext, tickCounter) -> {
            var context = new BetterDrawContext(client, vanillaContext);
            focusPicker.render(context, tickCounter);
            chargeDisplay.render(context, tickCounter);
        });

        ClientTickEvents.START_WORLD_TICK.register(world -> {
            if (client.isPaused()) return;

            focusPicker.tick();
            chargeDisplay.tick();
        });
    }

    public FocusPickerHudItem getFocusPicker() {
        return focusPicker;
    }

    public ChargeDisplayHudItem getChargeDisplay() {
        return chargeDisplay;
    }
}
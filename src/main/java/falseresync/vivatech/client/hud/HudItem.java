package falseresync.vivatech.client.hud;

import falseresync.lib.client.BetterDrawContext;
import net.minecraft.client.render.RenderTickCounter;

public interface HudItem {
    void render(BetterDrawContext context, RenderTickCounter tickCounter);

    void tick();
}
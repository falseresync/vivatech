package falseresync.vivatech.client.hud;

import falseresync.lib.client.BetterDrawContext;
import net.minecraft.client.DeltaTracker;

public interface HudItem {
    void render(BetterDrawContext context, DeltaTracker tickCounter);

    void tick();
}
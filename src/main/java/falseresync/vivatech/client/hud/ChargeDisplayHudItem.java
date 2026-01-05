package falseresync.vivatech.client.hud;

import com.mojang.blaze3d.systems.RenderSystem;
import falseresync.lib.client.BetterDrawContext;
import falseresync.lib.math.Easing;
import falseresync.vivatech.client.hud.HudItem;
import falseresync.vivatech.common.data.VivatechComponents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

import static falseresync.vivatech.common.Vivatech.vtId;

public class ChargeDisplayHudItem implements HudItem {
    protected static final Identifier TEX = vtId("textures/hud/charge_display.png");
    private static final int WIDGET_W = 16;
    private static final int WIDGET_H = 64;
    private static final int TEX_W = 32;
    private static final int TEX_H = 64;
    private static final int BAR_U = 0;
    private static final int BAR_V = 0;
    private static final int BAR_W = 16;
    private static final int BAR_H = 64;
    private static final int OVERLAY_X = 6;
    private static final int OVERLAY_Y = 16;
    private static final int OVERLAY_U = 16 + 6;
    private static final int OVERLAY_V = 16;
    private static final int OVERLAY_W = 3;
    private static final int OVERLAY_H = 32;
    private static final int ANIMATION_DURATION = 10;
    private final Minecraft client;
    private final Font textRenderer;
    private int currentCharge = 0;
    private int maxCharge = 0;
    private boolean isVisible = false;
    private ItemStack gadget;
    private boolean animating = false;
    private int remainingAnimationTicks = 0;

    public ChargeDisplayHudItem(Minecraft client, Font textRenderer) {
        this.client = client;
        this.textRenderer = textRenderer;
    }

    @Override
    public void render(BetterDrawContext context, DeltaTracker tickCounter) {
        if (isVisible() || animating) {
            float opacity = getAnimatedOpacity();
            float x = getAnimatedX();
            float y = context.guiHeight() / 2f - TEX_H / 2f;

            RenderSystem.enableBlend();
            context.setColor(1, 1, 1, opacity);

            context.drawNonDiscreteRect(TEX, x, y, BAR_U, BAR_V, BAR_W, BAR_H, TEX_W, TEX_H);

            var step = getStep();
            var overlayX = x + OVERLAY_X;
            var overlayY = y + OVERLAY_Y + step;
            var v = OVERLAY_V + step;
            var h = OVERLAY_H - step;
            context.drawNonDiscreteRect(TEX, overlayX, overlayY, OVERLAY_U, v, OVERLAY_W, h, TEX_W, TEX_H);

            RenderSystem.disableBlend();
        }
    }

    private float getAnimatedOpacity() {
        if (animating) {
            return isVisible()
                    ? 1 - (float) remainingAnimationTicks / ANIMATION_DURATION
                    : (float) remainingAnimationTicks / ANIMATION_DURATION;
        }
        return 1;
    }

    private float getAnimatedX() {
        if (animating) {
            return isVisible()
                    ? (float) (2 - remainingAnimationTicks * Easing.easeInOutCubic((double) remainingAnimationTicks / ANIMATION_DURATION))
                    : (float) (2 - (ANIMATION_DURATION - remainingAnimationTicks * Easing.easeInOutCubic((double) remainingAnimationTicks / ANIMATION_DURATION)));
        }

        return 2;
    }

    private int getStep() {
        return Math.clamp(Math.round(OVERLAY_H - (float) (currentCharge * OVERLAY_H) / maxCharge), 0, OVERLAY_H);
    }

    @Override
    public void tick() {
        if (client.player == null) {
            clear();
            return;
        }

        if (gadget != null) {
            currentCharge = gadget.getOrDefault(VivatechComponents.CHARGE, 0);
            maxCharge = gadget.getOrDefault(VivatechComponents.MAX_CHARGE, 0);
        }

        if (remainingAnimationTicks > 0) {
            remainingAnimationTicks -= 1;

            if (remainingAnimationTicks == 0) {
                animating = false;
                if (!isVisible()) {
                    clear();
                }
            }
        }
    }

    public void show() {
        if (!isVisible) {
            animate();
        }
        isVisible = true;
    }

    public void hide() {
        if (isVisible()) {
            animate();
        }
        isVisible = false;
        gadget = null;
    }

    private void clear() {
        isVisible = false;
        gadget = null;
        currentCharge = 0;
        maxCharge = 0;
    }

    private void animate() {
        animating = true;
        remainingAnimationTicks = ANIMATION_DURATION;
    }

    public void upload(ItemStack stack) {
        gadget = stack;
    }

    public boolean isVisible() {
        return isVisible && gadget != null;
    }

    public int getWidth() {
        return WIDGET_W;
    }
}

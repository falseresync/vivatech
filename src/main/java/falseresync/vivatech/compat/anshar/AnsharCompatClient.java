package falseresync.vivatech.compat.anshar;

import com.mojang.blaze3d.systems.RenderSystem;
import falseresync.lib.client.BetterDrawContext;
import falseresync.vivatech.client.ToolManager;
import falseresync.vivatech.client.hud.HudItem;
import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.item.VivatechItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import static falseresync.vivatech.common.Vivatech.vtId;

@Environment(EnvType.CLIENT)
public class AnsharCompatClient implements HudItem {
    private static final Identifier BASE_TEX = vtId("textures/world/comet_warp_beacon.png");
    private static final SpriteIdentifier CROWN_ID = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, vtId("world/comet_warp_beacon_crown"));
    private Sprite CROWN_SPRITE;
    public static AnsharCompatClient INSTANCE;
    private final MinecraftClient client;
    private boolean hidden = true;
    private int overlayMessageCooldown = 0;

    public AnsharCompatClient(MinecraftClient client) {
        this.client = client;
    }

    public static void init(MinecraftClient client) {
        INSTANCE = new AnsharCompatClient(client);

        HudRenderCallback.EVENT.register((vanillaContext, tickCounter) -> {
            var context = new BetterDrawContext(client, vanillaContext);
            INSTANCE.render(context, tickCounter);
        });

        ClientTickEvents.START_WORLD_TICK.register(world -> {
            if (client.isPaused()) return;

            INSTANCE.tick();
        });
    }

    @Override
    public void render(BetterDrawContext context, RenderTickCounter tickCounter) {
        if (hidden) {
            return;
        }

        if (CROWN_SPRITE == null) {
            CROWN_SPRITE = CROWN_ID.getSprite();
        }

        RenderSystem.enableBlend();

        var matrices = context.getMatrices();
        matrices.push();

        var centerX = context.getScaledWindowWidth() / 2f;
        var centerY = context.getScaledWindowHeight() / 2f;
        drawRing(12, 1f, 100, context, tickCounter, matrices, centerX, centerY);
        drawRing(12, 1.5f, -75, context, tickCounter, matrices, centerX, centerY);
        drawRing(18, 2f, 60, context, tickCounter, matrices, centerX, centerY);
        drawRing(24, 3f, -45, context, tickCounter, matrices, centerX, centerY);
        drawRing(28, 4f, 35, context, tickCounter, matrices, centerX, centerY);

        matrices.pop();

        RenderSystem.disableBlend();
    }

    private void drawRing(int tilesNo, float scale, float speedInverse, BetterDrawContext context, RenderTickCounter tickCounter, MatrixStack matrices, float centerX, float centerY) {
        matrices.push();
        matrices.multiplyPositionMatrix(new Matrix4f()
                .scaleAround(scale, scale, 1, centerX, centerY, 0)
                .rotateAround(new Quaternionf().rotateZ((client.world.getTime() + tickCounter.getTickDelta(false)) / speedInverse), centerX, centerY, 0));

        for (int i = 0; i < tilesNo; i++) {
            matrices.multiply(new Quaternionf().rotateZ(MathHelper.PI / tilesNo * 2), centerX, centerY, 0);
            context.drawNonDiscreteRect(BASE_TEX, centerX - 50, centerY - 50, 16, 16);
        }
        matrices.pop();
    }

    @Override
    public void tick() {
        if (client.player == null || client.world == null) {
            hidden = true;
            overlayMessageCooldown = 0;
            return;
        }

        var gadgetStack = ToolManager.scanInventoryForGadgets(client.player.getInventory());
        if (gadgetStack == null) {
            hidden = true;
            overlayMessageCooldown = 0;
            return;
        }

        hidden = !(gadgetStack.contains(AnsharCompat.NETWORK_UUID) && Vivatech.getChargeManager().hasEnoughCharge(gadgetStack, AnsharCompat.DEFAULT_COST, client.player));

        if (!hidden) {
            if (overlayMessageCooldown == 0) {
                client.inGameHud.setOverlayMessage(Text.translatable("tooltip.vivatech.gadget.anshar_compat.enter"), true);
                overlayMessageCooldown = 40;
            } else {
                overlayMessageCooldown -= 1;
            }
        } else {
            overlayMessageCooldown = 0;
        }
    }
}

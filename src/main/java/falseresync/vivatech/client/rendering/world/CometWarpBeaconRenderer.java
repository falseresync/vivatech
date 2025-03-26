package falseresync.vivatech.client.rendering.world;

import falseresync.lib.math.Color;
import falseresync.vivatech.client.rendering.RenderingUtil;
import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.data.VivatechAttachments;
import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.item.VivatechItemTags;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

import static falseresync.vivatech.common.Vivatech.vtId;

public class CometWarpBeaconRenderer implements WorldRenderEvents.AfterEntities {
    private static final RenderLayer BASE_LAYER = RenderLayer.getEntityTranslucentEmissive(vtId("textures/world/comet_warp_beacon.png"));
    private static final SpriteIdentifier CROWN_TEX = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, vtId("world/comet_warp_beacon_crown"));
    private static final int TINT_BASE = Color.ofHsv(0f, 0f, 1, 0.5f).argb();
    private static final int TINT_CROWN = Color.WHITE.argb();

    private static void drawBase(WorldRenderContext context, MatrixStack matrices, int light, int overlay) {
        var buffer = context.consumers().getBuffer(BASE_LAYER);

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));

        var rotation = (context.world().getTime() + context.tickCounter().getTickDelta(true)) / 20;
        var perPanelAdjustment = new Matrix4f()
                .rotateAround(RotationAxis.POSITIVE_Z.rotationDegrees(30), 0.5f, 0.5f, 0)
                .translate(0, 0, -0.02f);

        drawBasePart(matrices, buffer, light, overlay, rotation, RotationAxis.POSITIVE_Z, -0.01f, perPanelAdjustment);
        drawBasePart(matrices, buffer, light, overlay, rotation, RotationAxis.NEGATIVE_Z, -0.02f, perPanelAdjustment);

        matrices.pop();
    }

    private static void drawBasePart(MatrixStack matrices, VertexConsumer buffer, int light, int overlay, float rotation, RotationAxis rotationAxis, float initialOffset, Matrix4f perPanelAdjustment) {
        matrices.push();

        matrices.multiplyPositionMatrix(new Matrix4f().rotateAround(rotationAxis.rotation(rotation), 0.5f, 0.5f, 0));
        RenderingUtil.drawTexture(matrices, buffer, TINT_BASE, light, overlay, 0, 1, 0, 1, initialOffset, 0, 0.5f, 0, 0.5f);

        matrices.multiplyPositionMatrix(perPanelAdjustment);
        RenderingUtil.drawTexture(matrices, buffer, TINT_BASE, light, overlay, 0, 1, 0, 1, initialOffset, 0, 0.5f, 0, 0.5f);

        matrices.multiplyPositionMatrix(perPanelAdjustment);
        RenderingUtil.drawTexture(matrices, buffer, TINT_BASE, light, overlay, 0, 1, 0, 1, initialOffset, 0, 0.5f, 0, 0.5f);

        matrices.pop();
    }

    private static void drawCrown(WorldRenderContext context, MatrixStack matrices, int light, int overlay) {
        matrices.push();
        var adjustment = new Matrix4f()
                .rotateAround(RotationAxis.POSITIVE_X.rotationDegrees(180), 0, 0.5f, 0)
                .translate(0, 0.25f, -1)
                .scaleAround(0.5f, 0.5f, 0.5f, 0.5f);
        matrices.multiplyPositionMatrix(adjustment);

        var perPanelAdjustment = new Matrix4f().rotateAround(RotationAxis.POSITIVE_Y.rotationDegrees(60), 0.5f, 0, 0.5f);
        var sprite = CROWN_TEX.getSprite();
        var buffer = CROWN_TEX.getVertexConsumer(context.consumers(), RenderLayer::getEntityTranslucentEmissive);
        for (int i = 0; i < 6; i++) {
            matrices.multiplyPositionMatrix(perPanelAdjustment);
            RenderingUtil.drawSprite(matrices, buffer, sprite, TINT_BASE, light, overlay, 0, 1, 0, 1, -0.365f);
        }

        matrices.pop();
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void afterEntities(WorldRenderContext context) {
        var player = MinecraftClient.getInstance().player;
        if (!player.hasAttached(VivatechAttachments.HAS_INSPECTOR_GOGGLES)) {
            return;
        }

        var gadgetStack = player.getMainHandStack();
        if (!gadgetStack.isIn(VivatechItemTags.GADGETS)) {
            return;
        }

        var anchor = gadgetStack.get(VivatechComponents.WARP_FOCUS_ANCHOR);
        if (anchor == null) {
            anchor = gadgetStack.get(VivatechComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
        }
        if (anchor == null
                || anchor.dimension() != context.world().getRegistryKey()
                || !anchor.pos().isWithinDistance(player.getPos(), Vivatech.getConfig().inspectorGogglesDisplayRange * 4)
                || !context.frustum().isVisible(Box.from(anchor.pos().toCenterPos()))) {
            return;
        }

        var matrices = context.matrixStack();
        var light = WorldRenderer.getLightmapCoordinates(context.world(), anchor.pos().up());
        var overlay = OverlayTexture.DEFAULT_UV;

        matrices.push();

        // Adjust location
        var translation = context.camera().getPos().relativize(Vec3d.of(anchor.pos()));
        matrices.translate(translation.x, translation.y, translation.z);

        drawBase(context, matrices, light, overlay);
        drawCrown(context, matrices, light, overlay);

        matrices.pop();
    }
}

package falseresync.vivatech.client.rendering.world;

import falseresync.lib.math.Color;
import falseresync.vivatech.client.rendering.RenderingUtil;
import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.data.VivatechAttachments;
import falseresync.vivatech.common.data.VivatechComponents;
import falseresync.vivatech.common.item.VivatechItemTags;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

import static falseresync.vivatech.common.Vivatech.vtId;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

public class CometWarpBeaconRenderer implements WorldRenderEvents.AfterEntities {
    private static final RenderType BASE_LAYER = RenderTypes.entityTranslucentEmissive(vtId("textures/world/comet_warp_beacon.png"));
    private static final Material CROWN_TEX = new Material(TextureAtlas.LOCATION_BLOCKS, vtId("world/comet_warp_beacon_crown"));
    private static final int TINT_BASE = Color.ofHsv(0f, 0f, 1, 0.5f).argb();
    private static final int TINT_CROWN = Color.WHITE.argb();

    private static void drawBase(WorldRenderContext context, PoseStack matrices, int light, int overlay) {
        var buffer = context.consumers().getBuffer(BASE_LAYER);

        matrices.pushPose();
        matrices.mulPose(Axis.XP.rotationDegrees(90));

        var rotation = (context.world().getGameTime() + context.tickCounter().getGameTimeDeltaPartialTick(true)) / 20;
        var perPanelAdjustment = new Matrix4f()
                .rotateAround(Axis.ZP.rotationDegrees(30), 0.5f, 0.5f, 0)
                .translate(0, 0, -0.02f);

        drawBasePart(matrices, buffer, light, overlay, rotation, Axis.ZP, -0.01f, perPanelAdjustment);
        drawBasePart(matrices, buffer, light, overlay, rotation, Axis.ZN, -0.02f, perPanelAdjustment);

        matrices.popPose();
    }

    private static void drawBasePart(PoseStack matrices, VertexConsumer buffer, int light, int overlay, float rotation, Axis rotationAxis, float initialOffset, Matrix4f perPanelAdjustment) {
        matrices.pushPose();

        matrices.mulPose(new Matrix4f().rotateAround(rotationAxis.rotation(rotation), 0.5f, 0.5f, 0));
        RenderingUtil.drawTexture(matrices, buffer, TINT_BASE, light, overlay, 0, 1, 0, 1, initialOffset, 0, 0.5f, 0, 0.5f);

        matrices.mulPose(perPanelAdjustment);
        RenderingUtil.drawTexture(matrices, buffer, TINT_BASE, light, overlay, 0, 1, 0, 1, initialOffset, 0, 0.5f, 0, 0.5f);

        matrices.mulPose(perPanelAdjustment);
        RenderingUtil.drawTexture(matrices, buffer, TINT_BASE, light, overlay, 0, 1, 0, 1, initialOffset, 0, 0.5f, 0, 0.5f);

        matrices.popPose();
    }

    private static void drawCrown(WorldRenderContext context, PoseStack matrices, int light, int overlay) {
        matrices.pushPose();
        var adjustment = new Matrix4f()
                .rotateAround(Axis.XP.rotationDegrees(180), 0, 0.5f, 0)
                .translate(0, 0.25f, -1)
                .scaleAround(0.5f, 0.5f, 0.5f, 0.5f);
        matrices.mulPose(adjustment);

        var perPanelAdjustment = new Matrix4f().rotateAround(Axis.YP.rotationDegrees(60), 0.5f, 0, 0.5f);
        var sprite = CROWN_TEX.sprite();
        var buffer = CROWN_TEX.buffer(context.consumers(), RenderTypes::entityTranslucentEmissive);
        for (int i = 0; i < 6; i++) {
            matrices.mulPose(perPanelAdjustment);
            RenderingUtil.drawSprite(matrices, buffer, sprite, TINT_BASE, light, overlay, 0, 1, 0, 1, -0.365f);
        }

        matrices.popPose();
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void afterEntities(WorldRenderContext context) {
        var player = Minecraft.getInstance().player;
        if (!player.hasAttached(VivatechAttachments.HAS_INSPECTOR_GOGGLES)) {
            return;
        }

        var gadgetStack = player.getMainHandItem();
        if (!gadgetStack.is(VivatechItemTags.GADGETS)) {
            return;
        }

        var anchor = gadgetStack.get(VivatechComponents.WARP_FOCUS_ANCHOR);
        if (anchor == null) {
            anchor = gadgetStack.get(VivatechComponents.WARP_FOCUS_PERSISTENT_ANCHOR);
        }
        if (anchor == null
                || anchor.dimension() != context.world().dimension()
                || !anchor.pos().closerToCenterThan(player.position(), Vivatech.getConfig().inspectorGogglesDisplayRange * 4)
                || !context.frustum().isVisible(AABB.unitCubeFromLowerCorner(anchor.pos().getCenter()))) {
            return;
        }

        var matrices = context.matrices();
        var light = LevelRenderer.getLightColor(context.world(), anchor.pos().above());
        var overlay = OverlayTexture.NO_OVERLAY;

        matrices.pushPose();

        // Adjust location
        var translation = context.camera().getPosition().vectorTo(Vec3.atLowerCornerOf(anchor.pos()));
        matrices.translate(translation.x, translation.y, translation.z);

        drawBase(context, matrices, light, overlay);
        drawCrown(context, matrices, light, overlay);

        matrices.popPose();
    }
}

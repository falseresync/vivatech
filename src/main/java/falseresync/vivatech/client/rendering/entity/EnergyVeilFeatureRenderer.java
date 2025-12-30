package falseresync.vivatech.client.rendering.entity;

import falseresync.lib.math.VectorMath;
import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.data.VivatechAttachments;
import falseresync.vivatech.common.entity.EnergyVeilEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import static falseresync.vivatech.common.Vivatech.vtId;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

public class EnergyVeilFeatureRenderer<T extends Player> extends RenderLayer<T, PlayerModel<T>> {
    public static final ResourceLocation TEXTURE = vtId("textures/entity/energy_veil.png");
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(vtId("energy_veil"), "main");
    private final falseresync.vivatech.client.rendering.entity.EnergyVeilModel model;
    private final RenderType renderLayer;

    public EnergyVeilFeatureRenderer(RenderLayerParent<T, PlayerModel<T>> context, EntityModelSet loader) {
        super(context);
        model = new EnergyVeilModel(loader.bakeLayer(LAYER));
        renderLayer = RenderType.entityTranslucentEmissive(TEXTURE);
    }

    @Override
    public void render(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        var veil = findVeil(entity);
        if (veil == null) return;

        matrices.pushPose();
        var buffer = vertexConsumers.getBuffer(renderLayer);
        model.prepareMobModel(veil, limbAngle, limbDistance, tickDelta);

        for (int i = 0; i < 4; i++) {
            matrices.pushPose();
            matrices.mulPose(Axis.YP.rotationDegrees(i * 45));
            matrices.translate(-veil.getVisibleRadius(), -1, 0);
            model.renderToBuffer(matrices, buffer, light, OverlayTexture.NO_OVERLAY);
            matrices.translate(veil.getVisibleRadius() * 2, 0, 0);
            model.renderToBuffer(matrices, buffer, light, OverlayTexture.NO_OVERLAY);
            matrices.popPose();
        }
        matrices.popPose();
    }

    public void renderInFirstPerson(PoseStack matrices, MultiBufferSource vertexConsumers, int light, T entity, float tickDelta, float animationProgress) {
        var veil = findVeil(entity);
        if (veil == null) return;

        matrices.pushPose();
        matrices.setIdentity();

        var buffer = vertexConsumers.getBuffer(renderLayer);
        model.prepareMobModel(veil, 0, 0, tickDelta);

        var rotation = Minecraft.getInstance().gameRenderer.getMainCamera().rotation();
        var direction = Direction.UP.step();
        var swingAndTwist = VectorMath.swingTwistDecomposition(rotation, direction);
        matrices.mulPose(swingAndTwist.getRight());

        for (int i = 0; i < 3; i++) {
            matrices.pushPose();
            matrices.mulPose(Axis.YP.rotationDegrees(45 + i * 45));
            matrices.translate(veil.getVisibleRadius(), -1, 0);
            model.renderToBuffer(matrices, buffer, light, OverlayTexture.NO_OVERLAY, ((int) (0x44 / Vivatech.getConfig().fullscreenEffectsTransparency.modifier)) << 24 | 0x00_FF_FF_FF);
            matrices.popPose();
        }

        matrices.popPose();
    }

    @Nullable
    private EnergyVeilEntity findVeil(T entity) {
        return Optional.ofNullable(entity.getAttached(VivatechAttachments.ENERGY_VEIL_NETWORK_ID))
                .map(id -> entity.getCommandSenderWorld().getEntity(id))
                .flatMap(foundEntity -> foundEntity instanceof EnergyVeilEntity veil ? Optional.of(veil) : Optional.empty())
                .orElse(null);
    }

    public interface Accessor {
        EnergyVeilFeatureRenderer<AbstractClientPlayer> vivatech$getEnergyVeilRenderer();
    }
}

package falseresync.vivatech.client.rendering.block;

import falseresync.vivatech.common.block.WindmillBlock;
import falseresync.vivatech.common.blockentity.WindmillBlockEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import static falseresync.vivatech.common.Vivatech.vtId;

public class WindmillBlockEntityRenderer implements BlockEntityRenderer<WindmillBlockEntity> {
    public static final EntityModelLayer LAYER = new EntityModelLayer(vtId("windmill"), "main");
    public static final SpriteIdentifier TEX = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, vtId("block/windmill"));
    private final ModelPart model;

    public WindmillBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        model = ctx.getLayerModelPart(LAYER);
        model.setDefaultTransform(ModelTransform.pivot(8, 8, 0));
        model.resetTransform();
    }

    public static TexturedModelData getTexturedModelData() {
        var modelData = new ModelData();
        var root = modelData.getRoot();
        var blade = ModelPartBuilder.create().uv(0, 0).cuboid(-12, 0, 0, 12.0F, 60.0F, 2.0F);
        root.addChild("blade1", blade, ModelTransform.of(0, 0, 0, 0, MathHelper.HALF_PI, 0));
        root.addChild("blade2", blade, ModelTransform.of(0, 0, 0, 0, MathHelper.HALF_PI, 2 * MathHelper.PI / 3));
        root.addChild("blade3", blade, ModelTransform.of(0, 0, 0, 0, MathHelper.HALF_PI, 2 * 2 * MathHelper.PI / 3));
        return TexturedModelData.of(modelData, 32, 64);
    }

    @Override
    public void render(WindmillBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(entity.getCachedState().get(WindmillBlock.FACING).getOpposite().asRotation()), 0.5f, 0.5f, 0.5f);
        model.roll = entity.getRotationProgress(tickDelta);
        model.render(matrices, TEX.getVertexConsumer(vertexConsumers, RenderLayer::getEntityCutout), light, overlay);
        matrices.pop();
    }

    @Override
    public boolean rendersOutsideBoundingBox(WindmillBlockEntity blockEntity) {
        return true;
    }
}

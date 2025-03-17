package falseresync.vivatech.client.rendering;

import falseresync.lib.math.Color;
import falseresync.vivatech.common.Vivatech;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Objects;

public class RenderingUtil {
    public static final Vec3d UNIT_VEC3D = RenderingUtil.getSymmetricVec3d(1);

    public static Vec3d getSymmetricVec3d(double value) {
        return new Vec3d(value, value, value);
    }

    public static void levitateItemAboveBlock(World world, BlockPos pos, float tickDelta, ItemStack stack, ItemRenderer itemRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        levitateItemAboveBlock(world, pos, Vec3d.ZERO, RenderingUtil.UNIT_VEC3D, tickDelta, stack, itemRenderer, matrices, vertexConsumers);
    }

    public static void levitateItemAboveBlock(World world, BlockPos pos, float tickDelta, ItemStack stack, ModelTransformationMode mode, ItemRenderer itemRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        levitateItemAboveBlock(world, pos, Vec3d.ZERO, RenderingUtil.UNIT_VEC3D, tickDelta, stack, mode, itemRenderer, matrices, vertexConsumers);
    }

    public static void levitateItemAboveBlock(World world, BlockPos pos, Vec3d translation, Vec3d scale, float tickDelta, ItemStack stack, ItemRenderer itemRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        levitateItemAboveBlock(world, pos, translation, scale, tickDelta, stack, ModelTransformationMode.FIXED, itemRenderer, matrices, vertexConsumers);
    }

    public static void levitateItemAboveBlock(World world, BlockPos pos, Vec3d translation, Vec3d scale, float tickDelta, ItemStack stack, ModelTransformationMode mode, ItemRenderer itemRenderer, MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
        if (stack.isEmpty()) return;

        switch (Vivatech.getConfig().animationQuality) {
            case DEFAULT -> {
                matrices.push();

                var offset = MathHelper.sin((world.getTime() + tickDelta) / 16) / 16;
                matrices.translate(0.5 + translation.x, 1.25 + offset + translation.y, 0.5 + translation.z);
                matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(world.getTime() + tickDelta));

                scale = scale.multiply(stack.getItem() instanceof BlockItem ? 0.75 : 0.5);
                matrices.scale((float) scale.x, (float) scale.y, (float) scale.z);

                var lightAbove = WorldRenderer.getLightmapCoordinates(world, pos.up());
                itemRenderer.renderItem(stack, mode, lightAbove, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, world, 0);

                matrices.pop();
            }
            case FAST -> {
                matrices.push();
                var lightAbove = WorldRenderer.getLightmapCoordinates(world, pos.up());
                itemRenderer.renderItem(stack, mode, lightAbove, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, world, 0);
                matrices.pop();
            }
        }
    }

    public static void addParticle(World world, @Nullable ParticleEffect parameters, Vec3d position, Vec3d velocity) {
        if (parameters != null) {
            world.addParticle(parameters, position.x, position.y, position.z, velocity.x, velocity.y, velocity.z);
        }
    }

    public static void drawFluid(MatrixStack matrices, VertexConsumer buffer, BlockRenderView view, BlockPos pos, Fluid fluid, FluidState state, boolean still, int light, int overlay, float x, float width, float y, float height, float depth) {
        var handler = Objects.requireNonNull(FluidRenderHandlerRegistry.INSTANCE.get(fluid));
        var sprites = handler.getFluidSprites(view, pos, state);
        var tint = handler.getFluidColor(view, pos, state);
        drawTexturedSprite(matrices, buffer, still ? sprites[0] : sprites[1], Color.ofRgb(tint).argb(), light, overlay, x, width, y, height, depth);
    }

    /**
     * Use when RenderLayer requires a texture input (e.g. entity_* ones)
     */
    public static void drawSprite(MatrixStack matrices, VertexConsumer buffer, Sprite sprite, int tint, int light, int overlay, float x, float width, float y, float height, float depth) {
        drawTexture(matrices, buffer, tint, light, overlay, x, x + width, y, y + height, depth, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
    }

    /**
     * Use when RenderLayer doesn't require a texture input (e.g. cutout or translucent)
     */
    public static void drawTexturedSprite(MatrixStack matrices, VertexConsumer buffer, Sprite sprite, int tint, int light, int overlay, float x, float width, float y, float height, float depth) {
        drawTexture(matrices, sprite.getTextureSpecificVertexConsumer(buffer), tint, light, overlay, x, x + width, y, y + height, depth, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV());
    }

    public static void drawTexture(MatrixStack matrices, VertexConsumer buffer, int tint, int light, int overlay, float x, float width, float y, float height, float depth) {
        drawTexture(matrices, buffer, tint, light, overlay, x, x + width, y, y + height, depth);
    }

    public static void drawTexture(MatrixStack matrices, VertexConsumer buffer, int tint, int light, int overlay, float x1, float x2, float y1, float y2, float z, float u1, float u2, float v1, float v2) {
        var positionMatrix = matrices.peek().getPositionMatrix();
        buffer.vertex(positionMatrix, x1, y1, z).texture(u1, v1).color(tint).overlay(overlay).light(light).normal(0, 1, 0);
        buffer.vertex(positionMatrix, x1, y2, z).texture(u1, v2).color(tint).overlay(overlay).light(light).normal(0, 1, 0);
        buffer.vertex(positionMatrix, x2, y2, z).texture(u2, v2).color(tint).overlay(overlay).light(light).normal(0, 1, 0);
        buffer.vertex(positionMatrix, x2, y1, z).texture(u2, v1).color(tint).overlay(overlay).light(light).normal(0, 1, 0);
    }

    public static void drawQuad(VertexConsumer buffer, Matrix4f positionMatrix, Vector3f vUpLeft, Vector3f vDownLeft, Vector3f vDownRight, Vector3f vUpRight, float u1, float u2, float v1, float v2, int tint, int light, int overlay, Vector3f normal) {
        // Counter-clockwise https://stackoverflow.com/a/8142461
        setupVertex(vUpLeft, buffer, positionMatrix, u1, v1, tint, light, overlay, normal);
        setupVertex(vDownLeft, buffer, positionMatrix, u2, v1, tint, light, overlay, normal);
        setupVertex(vDownRight, buffer, positionMatrix, u2, v2, tint, light, overlay, normal);
        setupVertex(vUpRight, buffer, positionMatrix, u1, v2, tint, light, overlay, normal);
    }

    public static void setupVertex(Vector3f vertex, VertexConsumer buffer, Matrix4f positionMatrix, float u, float v, int tint, int light, int overlay, Vector3f normal) {
        buffer.vertex(positionMatrix, vertex.x, vertex.y, vertex.z).texture(u, v).color(tint).light(light).overlay(overlay).normal(normal.x, normal.y, normal.z);
    }
}

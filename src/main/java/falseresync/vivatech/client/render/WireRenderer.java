package falseresync.vivatech.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import falseresync.lib.math.VectorMath;
import falseresync.vivatech.client.VivatechClient;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static falseresync.vivatech.common.Vivatech.vtId;

public class WireRenderer implements WorldRenderEvents.AfterEntities {
    private static final SpriteIdentifier WIRE_TEX = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, vtId("block/wire"));

    @Override
    public void afterEntities(WorldRenderContext context) {
        var wires = VivatechClient.getClientWireManager().getWires();
        if (wires.isEmpty()) {
            return;
        }

        for (var wire : wires) {
            var matrices = context.matrixStack();
            matrices.push();

            // Adjust location
            var cameraPos = context.camera().getPos();
            var translation = cameraPos.relativize(wire.u().toCenterPos());
            matrices.translate(translation.x, translation.y, translation.z);

            var tint = Colors.WHITE;
            var light = WorldRenderer.getLightmapCoordinates(context.world(), BlockPos.ofFloored(wire.middle()));
            var overlay = OverlayTexture.DEFAULT_UV;

            var positionMatrix = matrices.peek().getPositionMatrix();
            var wireStart = new Vector3f(0, 0.1f, 0);
            var wireEnd = wire.u().toCenterPos().relativize(wire.v().toCenterPos()).toVector3f();
            var normal = cameraPos.subtract(wire.middle()).normalize().toVector3f();

            var sprite = WIRE_TEX.getSprite();
            var buffer = sprite.getTextureSpecificVertexConsumer(context.consumers().getBuffer(RenderLayer.getCutout()));

            matrices.multiply(VectorMath.swingTwistDecomposition(context.camera().getRotation(), wireEnd.normalize(new Vector3f())).getRight());

            var length = wireEnd.length();
            var segmentStart = new Vector3f(wireStart);
            var segmentEnd = wireEnd.mul(1f / length);
            var segmentStep = wireEnd.normalize(new Vector3f());
            for (int i = 0; i < length; i++) {
                drawSegment(buffer, positionMatrix, segmentStart, segmentEnd, sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV(), tint, light, overlay, normal);
                segmentStart.add(segmentStep);
                segmentEnd.add(segmentStep);
            }

            matrices.pop();
        }
    }

    private static void drawSegment(VertexConsumer buffer, Matrix4f positionMatrix, Vector3f vUpLeft, Vector3f vDownRight, float u1, float u2, float v1, float v2, int tint, int light, int overlay, Vector3f normal) {
        var vDownLeft = new Vector3f(vUpLeft.x, vDownRight.y, vUpLeft.z);
        var vUpRight = new Vector3f(vDownRight.x, vUpLeft.y, vDownRight.z);

        quad(buffer, positionMatrix, vUpLeft, vDownLeft, vDownRight, vUpRight, u1, u2, v1, v2, tint, light, overlay, normal);
        quad(buffer, positionMatrix, vUpRight, vDownRight, vDownLeft, vUpLeft, u1, u2, v1, v2, tint, light, overlay, normal.negate());
    }

    private static void quad(VertexConsumer buffer, Matrix4f positionMatrix, Vector3f vUpLeft, Vector3f vDownLeft, Vector3f vDownRight, Vector3f vUpRight, float u1, float u2, float v1, float v2, int tint, int light, int overlay, Vector3f normal) {
        setupVertex(vUpLeft, buffer, positionMatrix, u1, v1, tint, light, overlay, normal);
        setupVertex(vDownLeft, buffer, positionMatrix, u2, v1, tint, light, overlay, normal);
        setupVertex(vDownRight, buffer, positionMatrix, u2, v2, tint, light, overlay, normal);
        setupVertex(vUpRight, buffer, positionMatrix, u1, v2, tint, light, overlay, normal);
    }

    private static void setupVertex(Vector3f vertex, VertexConsumer buffer, Matrix4f positionMatrix, float u, float v, int tint, int light, int overlay, Vector3f normal) {
        buffer.vertex(positionMatrix, vertex.x, vertex.y, vertex.z).texture(u, v).color(tint).light(light).overlay(overlay).normal(normal.x, normal.y, normal.z);
    }
}

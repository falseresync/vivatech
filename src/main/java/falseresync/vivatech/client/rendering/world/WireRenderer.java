package falseresync.vivatech.client.rendering.world;

import falseresync.vivatech.client.VivatechClient;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Colors;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Random;
import java.util.function.Function;

import static falseresync.vivatech.common.Vivatech.vtId;

public class WireRenderer implements WorldRenderEvents.AfterEntities {
    private static final Random RANDOM = new Random();
    private static final float SEGMENT_SIZE = 1 / 32f;
    private static final float SEGMENT_SHIFT = 1 / 64f;

    private static final Vector3f VERTICAL_SEGMENT_NORMAL = Direction.NORTH.getUnitVector();
    private static final Vector3f VERTICAL_SEGMENT_SHIFT = new Vector3f(SEGMENT_SHIFT, 0, 0);
    private static final Vector3f NEG_VERTICAL_SEGMENT_SHIFT = new Vector3f(-SEGMENT_SHIFT, 0, 0);

    private static final Vector3f HORIZONTAL_SEGMENT_NORMAL = Direction.UP.getUnitVector();
    private static final Vector3f HORIZONTAL_SEGMENT_SHIFT = new Vector3f(0, SEGMENT_SHIFT, 0);
    private static final Vector3f NEG_HORIZONTAL_SEGMENT_SHIFT = new Vector3f(0, -SEGMENT_SHIFT, 0);

    private static final int TINT = Colors.WHITE;
    private static final int OVERLAY = OverlayTexture.DEFAULT_UV;

    private static final SpriteIdentifier SPRITE_ID = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, vtId("block/wire"));
    private Sprite sprite;
    private float[] defaultUv;
    private float segmentWidthOnAtlas;
    private float segmentHeightOnAtlas;
    private final Function<Integer, float[]> randomizedUv = Util.memoize(memoizationKey -> {
        var x = RANDOM.nextInt(8);
        var y = RANDOM.nextInt(8);
        return new float[] {
                defaultUv[0] + segmentWidthOnAtlas * x,
                defaultUv[0] + segmentWidthOnAtlas * (x + 1),
                defaultUv[2] + segmentHeightOnAtlas * y,
                defaultUv[2] + segmentHeightOnAtlas * (y + 1),
        };
    });

    @Override
    public void afterEntities(WorldRenderContext context) {
        var wires = VivatechClient.getClientWireManager().getWires(context.world().getRegistryKey());
        if (wires.isEmpty()) {
            return;
        }

        if (sprite == null) {
            sprite = SPRITE_ID.getSprite();
            defaultUv = new float[] {
                    sprite.getMinU(), sprite.getMaxU(), sprite.getMinV(), sprite.getMaxV()
            };
            segmentWidthOnAtlas = (defaultUv[1] - defaultUv[0]) / 8f;
            segmentHeightOnAtlas = (defaultUv[3] - defaultUv[2]) / 8f;
        }

        var matrices = context.matrixStack();
        var buffer = sprite.getTextureSpecificVertexConsumer(context.consumers().getBuffer(RenderLayer.getCutout()));
        var cameraPos = context.camera().getPos();

        for (var wire : wires) {
            var wireEnd = wire.start().relativize(wire.end()).toVector3f();
            var light = WorldRenderer.getLightmapCoordinates(context.world(), BlockPos.ofFloored(wire.middle()));

            matrices.push();

            var cameraAdjustment = cameraPos.relativize(wire.start());
            matrices.translate(cameraAdjustment.x, cameraAdjustment.y, cameraAdjustment.z);

            var positionMatrix = matrices.peek().getPositionMatrix();
            var direction = wireEnd.normalize(new Vector3f());

            float length = wireEnd.length();
            int segmentCount = (int) (length / SEGMENT_SIZE);

            if (direction.x == 0 && direction.z == 0) {
                drawVerticalWire(wireEnd, segmentCount, buffer, positionMatrix, light);
            } else {
                drawHorizontalWire(direction, wireEnd, segmentCount, length, buffer, positionMatrix, light);
            }

            matrices.pop();
        }
    }

    private void drawVerticalWire(Vector3f wireEnd, int segmentCount, VertexConsumer buffer, Matrix4f positionMatrix, int light) {
        var tangent = new Vector3f(VERTICAL_SEGMENT_NORMAL);
        var tangentialHalfSize = tangent.mul(SEGMENT_SIZE / 2f, new Vector3f());
        var stepY = new Vector3f(0, wireEnd.y / segmentCount, 0);

        var segmentAVertices = buildInitialSegmentVertices(tangentialHalfSize, VERTICAL_SEGMENT_SHIFT, stepY);
        var segmentBVertices = buildInitialSegmentVertices(tangentialHalfSize, NEG_VERTICAL_SEGMENT_SHIFT, stepY);

        for (int segmentNo = 0; segmentNo < segmentCount; segmentNo++) {
            for (var segmentVertex : segmentAVertices) {
                segmentVertex.add(stepY);
            }
            for (var segmentVertex : segmentBVertices) {
                segmentVertex.add(stepY);
            }

            var uv = randomizedUv.apply(segmentNo);
            drawSegment(buffer, positionMatrix, segmentAVertices, uv, TINT, light, OVERLAY, VERTICAL_SEGMENT_NORMAL);
            drawSegment(buffer, positionMatrix, segmentBVertices, uv, TINT, light, OVERLAY, VERTICAL_SEGMENT_NORMAL);
        }
    }

    private void drawHorizontalWire(Vector3f direction, Vector3f wireEnd, int segmentCount, float length, VertexConsumer buffer, Matrix4f positionMatrix, int light) {
        var tangent = new Vector3f(direction.x, 0, direction.z).normalize(new Vector3f()).cross(HORIZONTAL_SEGMENT_NORMAL);
        var tangentialHalfSize = tangent.mul(SEGMENT_SIZE / 2f, new Vector3f());

        var stepXZ = direction.mul(SEGMENT_SIZE, new Vector3f());
        float stepY = wireEnd.y / segmentCount;

        var segmentAVertices = buildInitialSegmentVertices(tangentialHalfSize, HORIZONTAL_SEGMENT_SHIFT, stepXZ);
        var segmentBVertices = buildInitialSegmentVertices(tangentialHalfSize, NEG_HORIZONTAL_SEGMENT_SHIFT, stepXZ);

        var startY = getSaggedY(0, stepY, length);
        for (int segmentNo = 0; segmentNo < segmentCount; segmentNo++) {
            var endY = getSaggedY(segmentNo + 1, stepY, length);
            advanceSegmentVertices(segmentAVertices, stepXZ, startY, endY, SEGMENT_SHIFT);
            advanceSegmentVertices(segmentBVertices, stepXZ, startY, endY, -SEGMENT_SHIFT);
            startY = endY;

            var uv = randomizedUv.apply(segmentNo);
            drawSegment(buffer, positionMatrix, segmentAVertices, uv, TINT, light, OVERLAY, HORIZONTAL_SEGMENT_NORMAL);
            drawSegment(buffer, positionMatrix, segmentBVertices, uv, TINT, light, OVERLAY, HORIZONTAL_SEGMENT_NORMAL);
        }
    }

    private float getSaggedY(float segmentNo, float stepY, float length) {
        return (float) (stepY * segmentNo + getSaggingCoefficient(length) * (Math.pow(2 * (SEGMENT_SIZE * segmentNo) - length, 2) / Math.pow(length, 2) - 1));
    }

    private float getSaggingCoefficient(float length) {
        return length < 5 ? 0.3f : 0.4f;
    }

    private void advanceSegmentVertices(Vector3f[] segmentAVertices, Vector3f stepXZ, float startY, float endY, float segmentRot) {
        for (var segmentVertex : segmentAVertices) {
            segmentVertex.add(stepXZ);
        }
        segmentAVertices[0].y = startY + segmentRot;
        segmentAVertices[1].y = startY - segmentRot;
        segmentAVertices[2].y = endY - segmentRot;
        segmentAVertices[3].y = endY + segmentRot;
    }

    private Vector3f[] buildInitialSegmentVertices(Vector3f tangentialHalfSize, Vector3f segmentShift, Vector3f step) {
        return new Vector3f[]{
                new Vector3f()          .sub(tangentialHalfSize).add(segmentShift),
                new Vector3f()          .add(tangentialHalfSize).sub(segmentShift),
                new Vector3f().add(step).add(tangentialHalfSize).sub(segmentShift),
                new Vector3f().add(step).sub(tangentialHalfSize).add(segmentShift),
        };
    }

    private void drawSegment(VertexConsumer buffer, Matrix4f positionMatrix, Vector3f[] vertices, float[] uv, int tint, int light, int overlay, Vector3f normal) {
        // Counter-clockwise - front-facing
        drawQuad(buffer, positionMatrix, vertices[0], vertices[1], vertices[2], vertices[3], uv[0], uv[1], uv[2], uv[3], tint, light, overlay, normal);
        // Clockwise - rear-facing
        drawQuad(buffer, positionMatrix, vertices[0], vertices[3], vertices[2], vertices[1], uv[1], uv[0], uv[3], uv[2], tint, light, overlay, normal.negate(new Vector3f()));
    }

    private void drawQuad(VertexConsumer buffer, Matrix4f positionMatrix, Vector3f vUpLeft, Vector3f vDownLeft, Vector3f vDownRight, Vector3f vUpRight, float u1, float u2, float v1, float v2, int tint, int light, int overlay, Vector3f normal) {
        // Counter-clockwise https://stackoverflow.com/a/8142461
        setupVertex(vUpLeft, buffer, positionMatrix, u1, v1, tint, light, overlay, normal);
        setupVertex(vDownLeft, buffer, positionMatrix, u2, v1, tint, light, overlay, normal);
        setupVertex(vDownRight, buffer, positionMatrix, u2, v2, tint, light, overlay, normal);
        setupVertex(vUpRight, buffer, positionMatrix, u1, v2, tint, light, overlay, normal);
    }

    private void setupVertex(Vector3f vertex, VertexConsumer buffer, Matrix4f positionMatrix, float u, float v, int tint, int light, int overlay, Vector3f normal) {
        buffer.vertex(positionMatrix, vertex.x, vertex.y, vertex.z).texture(u, v).color(tint).light(light).overlay(overlay).normal(normal.x, normal.y, normal.z);
    }
}

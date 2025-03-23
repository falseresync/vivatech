package falseresync.vivatech.client.rendering.world;

import falseresync.vivatech.client.VivatechClient;
import falseresync.vivatech.client.rendering.RenderingUtil;
import falseresync.vivatech.client.wire.WireParameters;
import falseresync.vivatech.client.wire.WireRenderingRegistry;
import falseresync.vivatech.client.wire.WireModel;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.*;
import net.minecraft.util.Colors;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class WireRenderer implements WorldRenderEvents.AfterEntities {
    private static final Vector3f VERTICAL_SEGMENT_NORMAL = Direction.NORTH.getUnitVector();//
    private static final Vector3f HORIZONTAL_SEGMENT_NORMAL = Direction.UP.getUnitVector();
    private static final int TINT = Colors.WHITE;
    private static final int OVERLAY = OverlayTexture.DEFAULT_UV;

    @Override
    public void afterEntities(WorldRenderContext context) {
        var wires = VivatechClient.getClientWireManager().getWires(context.world().getRegistryKey());
        if (wires.isEmpty()) {
            return;
        }

        var matrices = context.matrixStack();
        var cameraPos = context.camera().getPos();

        for (var wire : wires) {
            var parameters = WireRenderingRegistry.getAndBuild(wire);
            var model = parameters.getModel();
            var buffer = model.getSprite().getTextureSpecificVertexConsumer(context.consumers().getBuffer(RenderLayer.getCutout()));

            var wireEnd = wire.end().sub(wire.start(), new Vector3f());
            var light = WorldRenderer.getLightmapCoordinates(context.world(), BlockPos.ofFloored(wire.middle().x, wire.middle().y, wire.middle().z));

            matrices.push();

            var cameraAdjustment = wire.start().sub(cameraPos.toVector3f(), new Vector3f());
            matrices.translate(cameraAdjustment.x, cameraAdjustment.y, cameraAdjustment.z);

            var positionMatrix = matrices.peek().getPositionMatrix();
            var direction = wireEnd.normalize(new Vector3f());
            int segmentCount = (int) (wire.length() / model.getSegmentSize());

            if (direction.x == 0 && direction.z == 0) {
                drawVerticalWire(model, wireEnd, segmentCount, buffer, positionMatrix, light);
            } else {
                drawHorizontalWire(parameters, model, direction, wireEnd, segmentCount, wire.length(), buffer, positionMatrix, light);
            }

            matrices.pop();
        }
    }

    private void drawVerticalWire(WireModel parameters, Vector3f wireEnd, int segmentCount, VertexConsumer buffer, Matrix4f positionMatrix, int light) {
        var tangent = new Vector3f(VERTICAL_SEGMENT_NORMAL);
        var tangentialHalfSize = tangent.mul(parameters.getSegmentSize() / 2f, new Vector3f());
        var stepY = new Vector3f(0, wireEnd.y / segmentCount, 0);

        var segmentAVertices = buildInitialSegmentVertices(tangentialHalfSize, new Vector3f(parameters.getSegmentSize() / 2, 0, 0), stepY);
        var segmentBVertices = buildInitialSegmentVertices(tangentialHalfSize, new Vector3f(-parameters.getSegmentSize() / 2, 0, 0), stepY);

        for (int segmentNo = 0; segmentNo < segmentCount; segmentNo++) {
            for (var segmentVertex : segmentAVertices) {
                segmentVertex.add(stepY);
            }
            for (var segmentVertex : segmentBVertices) {
                segmentVertex.add(stepY);
            }

            var uv = parameters.getUv(segmentNo);
            drawSegment(buffer, positionMatrix, segmentAVertices, uv, TINT, light, OVERLAY, VERTICAL_SEGMENT_NORMAL);
            drawSegment(buffer, positionMatrix, segmentBVertices, uv, TINT, light, OVERLAY, VERTICAL_SEGMENT_NORMAL);
        }
    }

    private void drawHorizontalWire(WireParameters renderableWire, WireModel model, Vector3f direction, Vector3f wireEnd, int segmentCount, float length, VertexConsumer buffer, Matrix4f positionMatrix, int light) {
        var tangent = new Vector3f(direction.x, 0, direction.z).normalize(new Vector3f()).cross(HORIZONTAL_SEGMENT_NORMAL);
        var tangentialHalfSize = tangent.mul(model.getSegmentSize() / 2f, new Vector3f());

        var stepXZ = direction.mul(model.getSegmentSize(), new Vector3f());
        float stepY = wireEnd.y / segmentCount;

        var segmentAVertices = buildInitialSegmentVertices(tangentialHalfSize, new Vector3f(0, model.getSegmentSize() / 2, 0), stepXZ);
        var segmentBVertices = buildInitialSegmentVertices(tangentialHalfSize, new Vector3f(0, -model.getSegmentSize() / 2, 0), stepXZ);

        var startY = renderableWire.getSaggedY(0, stepY);
        for (int segmentNo = 0; segmentNo < segmentCount; segmentNo++) {
            var endY = renderableWire.getSaggedY(segmentNo + 1, stepY);
            advanceSegmentVertices(segmentAVertices, stepXZ, startY, endY, model.getSegmentSize() / 2);
            advanceSegmentVertices(segmentBVertices, stepXZ, startY, endY, -model.getSegmentSize() / 2);
            startY = endY;

            var uv = model.getUv(segmentNo);
            drawSegment(buffer, positionMatrix, segmentAVertices, uv, TINT, light, OVERLAY, HORIZONTAL_SEGMENT_NORMAL);
            drawSegment(buffer, positionMatrix, segmentBVertices, uv, TINT, light, OVERLAY, HORIZONTAL_SEGMENT_NORMAL);
        }
    }

    private void advanceSegmentVertices(Vector3f[] segmentAVertices, Vector3f stepXZ, float startY, float endY, float segmentShift) {
        for (var segmentVertex : segmentAVertices) {
            segmentVertex.add(stepXZ);
        }
        segmentAVertices[0].y = startY + segmentShift;
        segmentAVertices[1].y = startY - segmentShift;
        segmentAVertices[2].y = endY - segmentShift;
        segmentAVertices[3].y = endY + segmentShift;
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
        RenderingUtil.drawQuad(buffer, positionMatrix, vertices[0], vertices[1], vertices[2], vertices[3], uv[0], uv[1], uv[2], uv[3], tint, light, overlay, normal);
        // Clockwise - rear-facing
        RenderingUtil.drawQuad(buffer, positionMatrix, vertices[0], vertices[3], vertices[2], vertices[1], uv[1], uv[0], uv[3], uv[2], tint, light, overlay, normal.negate(new Vector3f()));
    }
}

package falseresync.vivatech.client.rendering.world;

import com.mojang.blaze3d.vertex.VertexConsumer;
import falseresync.vivatech.client.VivatechClient;
import falseresync.vivatech.client.wire.WireModel;
import falseresync.vivatech.client.wire.WireParameters;
import falseresync.vivatech.client.wire.WireRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.CommonColors;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class WireRenderer implements LevelRenderEvents.AfterEntities {
    private static final Vector3f VERTICAL_SEGMENT_NORMAL = Direction.NORTH.step();//
    private static final Vector3f HORIZONTAL_SEGMENT_NORMAL = Direction.UP.step();
    private static final int TINT = CommonColors.WHITE;
    private static final int OVERLAY = OverlayTexture.NO_OVERLAY;

    @Override
    public void afterEntities(LevelRenderContext context) {
        var world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }

        var wires = VivatechClient.getWiresManager().getFor(world.dimension());
        if (wires.isEmpty()) {
            return;
        }

        var matrices = context.poseStack();
        var cameraPos = context.gameRenderer().getMainCamera().position();
        var materialSet = Minecraft.getInstance().getAtlasManager();

        for (var wire : wires) {
            var parameters = WireRenderingRegistry.buildParameters(wire);
            var model = parameters.getModel();
            var buffer = model.getSprite(materialSet).wrap(context.bufferSource().getBuffer(RenderTypes.cutoutMovingBlock()));

            var wireEnd = wire.end().sub(wire.start(), new Vector3f());
            var light = LevelRenderer.getLightCoords(world, BlockPos.containing(wire.middle().x, wire.middle().y, wire.middle().z));

            matrices.pushPose();

            var cameraAdjustment = wire.start().sub(cameraPos.toVector3f(), new Vector3f());
            matrices.translate(cameraAdjustment.x, cameraAdjustment.y, cameraAdjustment.z);

            var positionMatrix = matrices.last().pose();
            var direction = wireEnd.normalize(new Vector3f());
            int segmentCount = (int) (wire.length() / model.getSegmentLength());

            if (direction.x == 0 && direction.z == 0) {
                drawVerticalWire(model, materialSet, wireEnd, segmentCount, buffer, positionMatrix, light);
            } else {
                drawHorizontalWire(parameters, model, materialSet, direction, wireEnd, segmentCount, buffer, positionMatrix, light);
            }

            matrices.popPose();
        }
    }

    private void drawVerticalWire(WireModel model, MaterialSet materialSet, Vector3f wireEnd, int segmentCount, VertexConsumer buffer, Matrix4f positionMatrix, int light) {
        var tangent = new Vector3f(VERTICAL_SEGMENT_NORMAL);
        var tangentialHalfSize = tangent.mul(model.getSegmentSize() / 2f, new Vector3f());
        var stepY = new Vector3f(0, wireEnd.y / segmentCount, 0);

        var segmentAVertices = buildInitialSegmentVertices(tangentialHalfSize, new Vector3f(model.getSegmentSize() / 2, 0, 0), stepY);
        var segmentBVertices = buildInitialSegmentVertices(tangentialHalfSize, new Vector3f(-model.getSegmentSize() / 2, 0, 0), stepY);

        for (int segmentNo = 0; segmentNo < segmentCount; segmentNo++) {
            for (var segmentVertex : segmentAVertices) {
                segmentVertex.add(stepY);
            }
            for (var segmentVertex : segmentBVertices) {
                segmentVertex.add(stepY);
            }

            var uv = model.getUv(materialSet, segmentNo);
            drawSegment(buffer, positionMatrix, segmentAVertices, uv, TINT, light, OVERLAY, VERTICAL_SEGMENT_NORMAL);
            drawSegment(buffer, positionMatrix, segmentBVertices, uv, TINT, light, OVERLAY, VERTICAL_SEGMENT_NORMAL);
        }
    }

    private void drawHorizontalWire(WireParameters renderableWire, WireModel model, MaterialSet materialSet, Vector3f direction, Vector3f wireEnd, int segmentCount, VertexConsumer buffer, Matrix4f positionMatrix, int light) {
        var tangent = new Vector3f(direction.x, 0, direction.z).normalize(new Vector3f()).cross(HORIZONTAL_SEGMENT_NORMAL);
        var tangentialHalfSize = tangent.mul(model.getSegmentSize() / 2f, new Vector3f());

        var stepXZ = direction.mul(model.getSegmentLength(), new Vector3f());
        float yPerSegment = wireEnd.y / segmentCount;

        var segmentAVertices = buildInitialSegmentVertices(tangentialHalfSize, new Vector3f(0, model.getSegmentSize() / 2, 0), stepXZ);
        var segmentBVertices = buildInitialSegmentVertices(tangentialHalfSize, new Vector3f(0, -model.getSegmentSize() / 2, 0), stepXZ);

        var startY = renderableWire.getSaggedYForSegment(0, 0);
        for (int segmentNo = 0; segmentNo < segmentCount; segmentNo++) {
            var endY = renderableWire.getSaggedYForSegment(yPerSegment * segmentNo, segmentNo + 1);
            advanceSegmentVertices(segmentAVertices, stepXZ, startY, endY, model.getSegmentSize() / 2);
            advanceSegmentVertices(segmentBVertices, stepXZ, startY, endY, -model.getSegmentSize() / 2);
            startY = endY;

            var uv = model.getUv(materialSet, segmentNo);
            drawSegment(buffer, positionMatrix, segmentAVertices, uv, TINT, light, OVERLAY, HORIZONTAL_SEGMENT_NORMAL);
            drawSegment(buffer, positionMatrix, segmentBVertices, uv, TINT, light, OVERLAY, HORIZONTAL_SEGMENT_NORMAL);
        }
    }

    private void advanceSegmentVertices(Vector3f[] vertices, Vector3f stepXZ, float startY, float endY, float segmentShift) {
        for (var segmentVertex : vertices) {
            segmentVertex.add(stepXZ);
        }
        vertices[0].y = startY + segmentShift;
        vertices[1].y = startY - segmentShift;
        vertices[2].y = endY - segmentShift;
        vertices[3].y = endY + segmentShift;
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

    public static void drawQuad(VertexConsumer buffer, Matrix4f positionMatrix, Vector3f vUpLeft, Vector3f vDownLeft, Vector3f vDownRight, Vector3f vUpRight, float u1, float u2, float v1, float v2, int tint, int light, int overlay, Vector3f normal) {
        // Counter-clockwise https://stackoverflow.com/a/8142461
        setupVertex(vUpLeft, buffer, positionMatrix, u1, v1, tint, light, overlay, normal);
        setupVertex(vDownLeft, buffer, positionMatrix, u2, v1, tint, light, overlay, normal);
        setupVertex(vDownRight, buffer, positionMatrix, u2, v2, tint, light, overlay, normal);
        setupVertex(vUpRight, buffer, positionMatrix, u1, v2, tint, light, overlay, normal);
    }

    public static void setupVertex(Vector3f vertex, VertexConsumer buffer, Matrix4f positionMatrix, float u, float v, int tint, int light, int overlay, Vector3f normal) {
        buffer.addVertex(positionMatrix, vertex.x, vertex.y, vertex.z).setUv(u, v).setColor(tint).setLight(light).setOverlay(overlay).setNormal(normal.x, normal.y, normal.z);
    }
}

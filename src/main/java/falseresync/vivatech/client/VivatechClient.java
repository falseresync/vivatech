package falseresync.vivatech.client;

import com.mojang.blaze3d.systems.RenderSystem;
import falseresync.vivatech.network.VtClientReceivers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.render.*;
import net.minecraft.util.Colors;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class VivatechClient implements ClientModInitializer {
    private static PowerSystemsRenderManager powerSystemsRenderManager;

    @Override
    public void onInitializeClient() {
        VtClientReceivers.registerAll();

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            powerSystemsRenderManager = new PowerSystemsRenderManager(client);
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            powerSystemsRenderManager.tick();
        });

        ClientChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
            if (powerSystemsRenderManager != null) {
                powerSystemsRenderManager.queueUnsyncedChunk(chunk.getPos());
            }
        });

        WorldRenderEvents.AFTER_ENTITIES.register(context -> {
            var wires = powerSystemsRenderManager.getWires();
            if (wires.isEmpty()) {
                return;
            }
            for (var wire : wires) {
                var matrices = context.matrixStack();
                matrices.push();

                // Adjust location
                var translation = context.camera().getPos().relativize(wire.from().toCenterPos());
                matrices.translate(translation.x, translation.y, translation.z);

//                var light = WorldRenderer.getLightmapCoordinates(context.world(), conn.from());
//                var overlay = OverlayTexture.DEFAULT_UV;

                var tessellator = Tessellator.getInstance();
                var buffer = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.LINES);

                var tint = Colors.BLACK;
                var x1 = 0;
                var y1 = 0;
                var z1 = 0;
                var vertex2 = wire.from().toCenterPos().relativize(wire.to().toCenterPos()).toVector3f();
                var normal = context.camera().getPos().subtract(wire.middle()).normalize().toVector3f();
                var normalNeg = normal.negate();

                var rotation = context.camera().getRotation();
                var direction = vertex2.normalize(new Vector3f());
                var rotationAxis = new Vector3f(rotation.x, rotation.y, rotation.z);
                var projection = direction.mul(rotationAxis.lengthSquared() * rotationAxis.angleCos(direction));
                var twist = new Quaternionf(projection.x, projection.y, projection.z, rotation.w).normalize();
                var swing = rotation.mul(twist.conjugate(new Quaternionf()), new Quaternionf());

                matrices.multiply(twist);

                var positionMatrix = matrices.peek().getPositionMatrix();

                buffer.vertex(positionMatrix, x1, y1, z1).color(tint).normal(normal.x, normal.y, normal.z);
                buffer.vertex(positionMatrix, vertex2.x, vertex2.y, vertex2.z).color(tint).normal(normal.x, normal.y, normal.z);
                buffer.vertex(positionMatrix, x1, y1 + 0.1f, z1).color(tint).normal(normal.x, normal.y, normal.z);
                buffer.vertex(positionMatrix, vertex2.x, vertex2.y + 0.1f, vertex2.z).color(tint).normal(normal.x, normal.y, normal.z);

                buffer.vertex(positionMatrix, x1, y1 + 0.1f, z1).color(tint).normal(normalNeg.x, normalNeg.y, normalNeg.z);
                buffer.vertex(positionMatrix, vertex2.x, vertex2.y, vertex2.z).color(tint).normal(normalNeg.x, normalNeg.y, normalNeg.z);
                buffer.vertex(positionMatrix, x1, y1, z1).color(tint).normal(normalNeg.x, normalNeg.y, normalNeg.z);

                RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
                RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

                BufferRenderer.drawWithGlobalProgram(buffer.end());

                matrices.pop();
            }
        });
    }

    public static PowerSystemsRenderManager getPowerSystemsRenderManager() {
        return powerSystemsRenderManager;
    }
}

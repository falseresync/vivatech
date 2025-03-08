package falseresync.lib.client;

import com.mojang.blaze3d.systems.*;
import net.minecraft.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.render.*;
import net.minecraft.util.*;
import org.joml.*;

public class BetterDrawContext extends DrawContext {
    public BetterDrawContext(MinecraftClient client, DrawContext context) {
        super(client, context.getVertexConsumers());
    }

    public void drawSquare(Identifier texture, int x, int y, int size) {
        drawSquare(texture, x, y, size, size);
    }

    public void drawSquare(Identifier texture, int x, int y, int size, int textureSize) {
        drawRect(texture, x, y, size, size, textureSize, textureSize);
    }

    public void drawRect(Identifier texture, int x, int y, int width, int height) {
        drawRect(texture, x, y, width, height, 16, 16);
    }

    public void drawRect(Identifier texture, int x, int y, int width, int height, int texWidth, int texHeight) {
        drawTexture(texture, x, y, 0, 0, width, height, texWidth, texHeight);
    }

    public void drawTexturedQuad(Identifier texture, float x1, float x2, float y1, float y2, float u1, float u2, float v1, float v2) {
        float z = 0;
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        Matrix4f matrix4f = getMatrices().peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        bufferBuilder.vertex(matrix4f, x1, y1, z).texture(u1, v1);
        bufferBuilder.vertex(matrix4f, x1, y2, z).texture(u1, v2);
        bufferBuilder.vertex(matrix4f, x2, y2, z).texture(u2, v2);
        bufferBuilder.vertex(matrix4f, x2, y1, z).texture(u2, v1);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
    }

    public void drawNonDiscreteRect(Identifier texture, float x, float y, int u, int v, int regionW, int regionH, int texW, int texH) {
        drawTexturedQuad(texture,
                x + u, x + u + regionW,
                y + v, y + v + regionH,
                (float) u / texW, (float) (u + regionW) / texW,
                (float) v / texH, (float) (v + regionH) / texH);
    }

    public void drawNonDiscreteRect(Identifier texture, float x, float y, int texW, int texH) {
        drawNonDiscreteRect(texture, x, y, 0, 0, texW, texH, texW, texH);
    }
}

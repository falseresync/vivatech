package falseresync.lib.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class BetterDrawContext extends GuiGraphics {
    public BetterDrawContext(Minecraft client, GuiGraphics context) {
        super(client, context.bufferSource());
    }

    public void drawSquare(ResourceLocation texture, int x, int y, int size) {
        drawSquare(texture, x, y, size, size);
    }

    public void drawSquare(ResourceLocation texture, int x, int y, int size, int textureSize) {
        drawRect(texture, x, y, size, size, textureSize, textureSize);
    }

    public void drawRect(ResourceLocation texture, int x, int y, int width, int height) {
        drawRect(texture, x, y, width, height, 16, 16);
    }

    public void drawRect(ResourceLocation texture, int x, int y, int width, int height, int texWidth, int texHeight) {
        blit(texture, x, y, 0, 0, width, height, texWidth, texHeight);
    }

    public void drawTexturedQuad(ResourceLocation texture, float x1, float x2, float y1, float y2, float u1, float u2, float v1, float v2) {
        float z = 0;
        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = pose().last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.addVertex(matrix4f, x1, y1, z).setUv(u1, v1);
        bufferBuilder.addVertex(matrix4f, x1, y2, z).setUv(u1, v2);
        bufferBuilder.addVertex(matrix4f, x2, y2, z).setUv(u2, v2);
        bufferBuilder.addVertex(matrix4f, x2, y1, z).setUv(u2, v1);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
    }

    public void drawNonDiscreteRect(ResourceLocation texture, float x, float y, int u, int v, int regionW, int regionH, int texW, int texH) {
        drawTexturedQuad(texture,
                x, x + regionW,
                y, y + regionH,
                (float) u / texW, (float) (u + regionW) / texW,
                (float) v / texH, (float) (v + regionH) / texH);
    }

    public void drawNonDiscreteRect(ResourceLocation texture, float x, float y, int texW, int texH) {
        drawNonDiscreteRect(texture, x, y, 0, 0, texW, texH, texW, texH);
    }
}

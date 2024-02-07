package dev.falseresync.vivatech.common.screen.widget;

import io.github.cottonmc.cotton.gui.client.ScreenDrawing;
import io.github.cottonmc.cotton.gui.widget.WSprite;
import io.github.cottonmc.cotton.gui.widget.data.Texture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class FixedWSprite extends WSprite {
    /**
     * Create a new sprite with a single image.
     * @param texture The image texture to display.
     * @since 3.0.0
     */
    public FixedWSprite(Texture texture) {
        super(texture);
    }

    /**
     * Create a new sprite with a single image.
     * @param image The location of the image to display.
     */
    public FixedWSprite(Identifier image) {
        this(new Texture(image));
    }

    /**
     * Create a new sprite with a single image and custom UV values.
     *
     * @param image The location of the image to display.
     * @param u1 the left edge of the texture
     * @param v1 the top edge of the texture
     * @param u2 the right edge of the texture
     * @param v2 the bottom edge of the texture
     */
    public FixedWSprite(Identifier image, float u1, float v1, float u2, float v2) {
        this(new Texture(image, u1, v1, u2, v2));
    }

    /**
     * Create a new animated sprite.
     * @param frameTime How long in milliseconds to display for. (1 tick = 50 ms)
     * @param frames The locations of the frames of the animation.
     */
    public FixedWSprite(int frameTime, Identifier... frames) {
        super(frameTime, frames);
    }

    /**
     * Create a new animated sprite.
     * @param frameTime How long in milliseconds to display for. (1 tick = 50 ms)
     * @param frames The locations of the frames of the animation.
     * @since 3.0.0
     */
    public FixedWSprite(int frameTime, Texture... frames) {
        super(frameTime, frames);
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void paint(DrawContext context, int x, int y, int mouseX, int mouseY) {
        if (singleImage) {
            paintFrame(context, x, y, frames[0]);
        } else {
            //grab the system time at the very start of the frame.
            long now = System.nanoTime() / 1_000_000L;

            //check bounds so the Identifier isn't passed a bad number
            boolean inBounds = (currentFrame >= 0) && (currentFrame < frames.length);
            if (!inBounds) currentFrame = 0;
            //assemble and draw the frame calculated last iteration.
            Texture currentFrameTex = frames[currentFrame];
            paintFrame(context, x, y, currentFrameTex);

            //calculate how much time has elapsed since the last animation change, and change the frame if necessary.
            long elapsed = now - lastFrame;
            currentFrameTime += elapsed;
            if (currentFrameTime >= frameTime) {
                currentFrame++;
                //if we've hit the end of the animation, go back to the beginning
                if (currentFrame >= frames.length) {
                    currentFrame = 0;
                }
                currentFrameTime = 0;
            }

            //frame is over; this frame is becoming the last frame so write the time to lastFrame
            this.lastFrame = now;
        }
    }
}

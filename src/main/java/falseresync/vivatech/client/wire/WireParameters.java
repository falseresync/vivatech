package falseresync.vivatech.client.wire;

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;

public interface WireParameters {
    SpriteIdentifier getSpriteId();

    Sprite getSprite();

    float[] getUv(int segmentNo);

    float getSegmentSize();

    float getSaggedY(int segmentNo, float yStep, float length);

    float getSaggingCoefficient(float length);
}

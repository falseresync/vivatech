package falseresync.vivatech.client.wire;

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;

public interface WireModel {
    SpriteIdentifier getSpriteId();

    Sprite getSprite();

    float[] getUv(int segmentNo);

    float getSegmentSize();

    default float getSegmentLength() {
        return getSegmentSize() * 4;
    }
}

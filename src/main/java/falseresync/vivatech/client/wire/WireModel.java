package falseresync.vivatech.client.wire;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;

public interface WireModel {
    Material getSpriteId();

    TextureAtlasSprite getSprite();

    float[] getUv(int segmentNo);

    float getSegmentSize();

    default float getSegmentLength() {
        return getSegmentSize() * 4;
    }
}

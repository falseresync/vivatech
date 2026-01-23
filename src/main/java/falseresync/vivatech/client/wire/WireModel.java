package falseresync.vivatech.client.wire;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;

public interface WireModel {
    Material getSpriteId();

    TextureAtlasSprite getSprite(MaterialSet materialSet);

    float[] getUv(MaterialSet materialSet, int segmentNo);

    float getSegmentSize();

    default float getSegmentLength() {
        return getSegmentSize() * 4;
    }
}

package falseresync.vivatech.client.wire;

import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;

import static falseresync.vivatech.common.Vivatech.vtId;

public class CopperWireParameters extends RandomizedUvWireParameters {
    private static final float SEGMENT_SIZE = 1 / 32f;
    private static final SpriteIdentifier SPRITE_ID = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, vtId("block/wire"));

    public CopperWireParameters() {
        super(SPRITE_ID, 2f);
    }

    @Override
    public float getSegmentSize() {
        return SEGMENT_SIZE;
    }

    @Override
    public float getSaggedY(int segmentNo, float yStep, float length) {
        return (float) (yStep * segmentNo + getSaggingCoefficient(length) * (Math.pow(2 * (SEGMENT_SIZE * segmentNo) - length, 2) / Math.pow(length, 2) - 1));
    }

    @Override
    public float getSaggingCoefficient(float length) {
        return length < 5 ? 0.3f : 0.4f;
    }
}

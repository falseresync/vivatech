package falseresync.vivatech.client.wire;

import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.SpriteIdentifier;
import net.minecraft.util.Util;

import java.util.Random;
import java.util.function.Function;

public class RandomizedUvWireModel implements WireModel {
    private static final Random RANDOM = new Random();
    protected final float segmentSize;
    private final SpriteIdentifier spriteId;
    private int uvChunkAmount;
    private Sprite sprite;
    private float[] defaultUv;
    private float segmentWidthOnAtlas;
    private float segmentHeightOnAtlas;
    private final Function<Integer, float[]> randomizedUv = Util.memoize(memoizationKey -> {
        var x = RANDOM.nextInt(uvChunkAmount);
        var y = RANDOM.nextInt(uvChunkAmount);
        return new float[] {
                defaultUv[0] + segmentWidthOnAtlas * x,
                defaultUv[0] + segmentWidthOnAtlas * (x + 1),
                defaultUv[2] + segmentHeightOnAtlas * y,
                defaultUv[2] + segmentHeightOnAtlas * (y + 1),
        };
    });

    protected RandomizedUvWireModel(SpriteIdentifier spriteId, float uvChunkSize, float segmentSize) {
        this.spriteId = spriteId;
        this.uvChunkAmount = (int) (16 / uvChunkSize);
        this.segmentSize = segmentSize;
    }

    @Override
    public SpriteIdentifier getSpriteId() {
        return spriteId;
    }

    @Override
    public Sprite getSprite() {
        if (sprite == null) {
            sprite = getSpriteId().getSprite();
        }
        return sprite;
    }

    @Override
    public float[] getUv(int segmentNo) {
        if (defaultUv == null) {
            defaultUv = new float[] {
                    getSprite().getMinU(), getSprite().getMaxU(), getSprite().getMinV(), getSprite().getMaxV()
            };
            segmentWidthOnAtlas = (defaultUv[1] - defaultUv[0]) / uvChunkAmount;
            segmentHeightOnAtlas = (defaultUv[3] - defaultUv[2]) / uvChunkAmount;
        }
        return randomizedUv.apply(segmentNo);
    }

    @Override
    public float getSegmentSize() {
        return segmentSize;
    }
}

package falseresync.vivatech.client.wire;

import java.util.Random;
import java.util.function.Function;

import falseresync.vivatech.client.wire.WireModel;
import net.minecraft.Util;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;

public class RandomizedUvWireModel implements WireModel {
    private static final Random RANDOM = new Random();
    protected final float segmentSize;
    private final Material spriteId;
    private int uvChunkAmount;
    private TextureAtlasSprite sprite;
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

    protected RandomizedUvWireModel(Material spriteId, float uvChunkSize, float segmentSize) {
        this.spriteId = spriteId;
        this.uvChunkAmount = (int) (16 / uvChunkSize);
        this.segmentSize = segmentSize;
    }

    @Override
    public Material getSpriteId() {
        return spriteId;
    }

    @Override
    public TextureAtlasSprite getSprite() {
        if (sprite == null) {
            sprite = getSpriteId().sprite();
        }
        return sprite;
    }

    @Override
    public float[] getUv(int segmentNo) {
        if (defaultUv == null) {
            defaultUv = new float[] {
                    getSprite().getU0(), getSprite().getU1(), getSprite().getV0(), getSprite().getV1()
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

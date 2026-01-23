package falseresync.vivatech.client.wire;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.util.Util;
import org.jspecify.annotations.Nullable;

import java.util.Random;
import java.util.function.Function;

public class RandomizedUvWireModel implements WireModel {
    private static final Random RANDOM = new Random();
    protected final float segmentSize;
    private final Material spriteId;
    private int uvChunkAmount;
    @Nullable
    private TextureAtlasSprite sprite;
    private float @Nullable [] defaultUv = null;
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
    public TextureAtlasSprite getSprite(MaterialSet materialSet) {
        if (sprite == null) {
            sprite = materialSet.get(getSpriteId());
        }
        return sprite;
    }

    @Override
    public float[] getUv(MaterialSet materialSet, int segmentNo) {
        if (defaultUv == null) {
            defaultUv = new float[] {
                    getSprite(materialSet).getU0(), getSprite(materialSet).getU1(), getSprite(materialSet).getV0(), getSprite(materialSet).getV1()
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

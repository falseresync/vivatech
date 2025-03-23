package falseresync.vivatech.client.wire;

import com.google.common.base.Preconditions;
import falseresync.vivatech.common.power.wire.Wire;
import falseresync.vivatech.common.power.wire.WireType;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.client.util.SpriteIdentifier;

import java.util.Map;

import static falseresync.vivatech.common.Vivatech.vtId;

public class WireRenderingRegistry {
    private static final SpriteIdentifier COPPER_SPRITE_ID = new SpriteIdentifier(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE, vtId("block/wire"));
    private static final Map<WireType, WireModel> MODELS = new Reference2ObjectArrayMap<>();
    private static final Map<WireType, WireParameters.Factory> FACTORIES = new Reference2ObjectArrayMap<>();

    public static WireParameters getAndBuild(Wire wire) {
        return FACTORIES.get(wire.type()).build(wire, MODELS.get(wire.type()));
    }

    public static void registerModel(WireType type, WireModel parameters) {
        Preconditions.checkArgument(!FACTORIES.containsKey(type), "Attempted to register a model for the same wire type twice!");
        MODELS.put(type, parameters);
    }

    public static void registerParametersFactory(WireType type, WireParameters.Factory factory) {
        Preconditions.checkArgument(!FACTORIES.containsKey(type), "Attempted to register a parameters factory for the same wire type twice!");
        FACTORIES.put(type, factory);
    }

    public static void registerAll() {
        registerModel(WireType.V_230, new RandomizedUvWireModel(COPPER_SPRITE_ID, 2f, 1/32f));

        registerParametersFactory(WireType.V_230, (wire, parameters) -> new DefaultRenderableWire(wire, parameters) {
            @Override
            public float getSaggingCoefficient() {
                return wire.length() < 5 ? 0.3f : 0.4f;
            }
        });
    }
}

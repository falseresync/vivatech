package falseresync.vivatech.client.wire;

import com.google.common.base.Preconditions;
import falseresync.vivatech.Vivatech;
import falseresync.vivatech.world.electricity.wire.Wire;
import falseresync.vivatech.world.electricity.wire.WireType;
import it.unimi.dsi.fastutil.objects.Reference2ObjectArrayMap;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;

import java.util.Map;


public class WireRenderingRegistry {
    private static final Material COPPER_SPRITE_ID = new Material(TextureAtlas.LOCATION_BLOCKS, Vivatech.id("block/wire"));
    private static final Map<WireType, WireModel> MODELS = new Reference2ObjectArrayMap<>();
    private static final Map<WireType, WireParameters.Factory> FACTORIES = new Reference2ObjectArrayMap<>();

    public static WireParameters buildParameters(Wire wire) {
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

    public static void init() {
        registerModel(WireType.V_230, new RandomizedUvWireModel(COPPER_SPRITE_ID, 2f, 1/32f));

        registerParametersFactory(WireType.V_230, (wire, parameters) -> new SimpleWireParameters(wire, parameters) {
            @Override
            public float getSaggingCoefficient() {
                return wire.length() < 5 ? 0.3f : 0.4f;
            }
        });
    }
}

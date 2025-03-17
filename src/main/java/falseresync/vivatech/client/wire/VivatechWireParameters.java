package falseresync.vivatech.client.wire;

import falseresync.vivatech.common.power.wire.Wire;
import falseresync.vivatech.common.power.wire.WireType;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import java.util.Map;

public class VivatechWireParameters {
    private static final Map<WireType, WireParameters> PARAMETERS = new Object2ObjectArrayMap<>();

    public static WireParameters get(Wire wire) {
        return PARAMETERS.get(wire.type());
    }

    public static void register(WireType type, WireParameters parameters) {
        PARAMETERS.put(type, parameters);
    }

    public static void registerAll() {
        register(WireType.V_230, new CopperWireParameters());
    }
}

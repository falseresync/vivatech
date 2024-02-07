package dev.falseresync.vivatech.common.power;

import dev.falseresync.vivatech.api.power.PowerGridNode;
import dev.falseresync.vivatech.api.power.PowerGridNodeProvider;
import dev.falseresync.vivatech.api.power.PowerSystem;

public class VivatechPowerSystem {
    public static void init() {
        PowerSystem.NODE_LOOKUP.registerFallback((world, pos, state, blockEntity, context) -> {
            if (blockEntity != null) {
                if (blockEntity instanceof PowerGridNode node) {
                    return node;
                }

                if (blockEntity instanceof PowerGridNodeProvider provider) {
                    return provider.getPowerGridNode(world, pos);
                }
            }

            if (state.getBlock() instanceof PowerGridNodeProvider provider) {
                return provider.getPowerGridNode(world, pos);
            }

            return null;
        });
    }
}

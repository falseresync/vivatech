package dev.falseresync.vivatech.api.power;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;

import static dev.falseresync.vivatech.common.VivatechConsts.vivatech;

public class PowerSystem {
    public static final BlockApiLookup<PowerGridNode, Void> NODE_LOOKUP =
            BlockApiLookup.get(vivatech("power_grid_node_lookup"), PowerGridNode.class, Void.class);
}

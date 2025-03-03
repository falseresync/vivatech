package falseresync.vivatech.common.power;

import falseresync.vivatech.common.blockentity.VtBlockEntities;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import static falseresync.vivatech.common.Vivatech.vtId;

public class PowerSystem {
    public static final BlockApiLookup<Appliance, Void> APPLIANCE = BlockApiLookup.get(vtId("power_node"), Appliance.class, Void.class);
    public static final WorldSavePath SAVE_PATH = new WorldSavePath("power_systems");
    public static final int DATA_VERSION = 100;

    public static Map<ChunkPos, Set<Wire>> createWireMap() {
        return new Object2ObjectRBTreeMap<>(Comparator.comparingLong(ChunkPos::toLong));
    }

    public static void registerAll() {
        APPLIANCE.registerSelf(VtBlockEntities.WIRE_POST);
    }

    public static String createFileName(World world) {
        return world.getDimensionEntry().getIdAsString().replace(':', '_').replace('/', '_');
    }
}

package falseresync.vivatech.common.power;

import falseresync.vivatech.common.block.VivatechBlocks;
import falseresync.vivatech.common.blockentity.VivatechBlockEntities;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import static falseresync.vivatech.common.Vivatech.vtId;

public class PowerSystem {
    public static final BlockApiLookup<Appliance, Direction> APPLIANCE = BlockApiLookup.get(vtId("appliance"), Appliance.class, Direction.class);
    public static final BlockApiLookup<GridVertex, Void> GRID_VERTEX = BlockApiLookup.get(vtId("grid_vertex"), GridVertex.class, Void.class);
    public static final WorldSavePath SAVE_PATH = new WorldSavePath("power_systems");
    public static final int DATA_VERSION = 100;

    public static <T> Map<ChunkPos, Set<T>> createChunkPosKeyedMap() {
        return new Object2ObjectRBTreeMap<>(Comparator.comparingLong(ChunkPos::toLong));
    }

    public static <T> Set<ChunkPos> createChunkPosSet() {
        return new ObjectRBTreeSet<>(Comparator.comparingLong(ChunkPos::toLong));
    }

    public static void registerAll() {
        APPLIANCE.registerSelf(
                VivatechBlockEntities.GENERATOR,
                VivatechBlockEntities.HEATER,
                VivatechBlockEntities.CHARGER,
                VivatechBlockEntities.STATIC_COMPENSATOR
        );

        GRID_VERTEX.registerForBlocks((world, pos, state, blockEntity, context) -> {
            if (state.getBlock() instanceof GridVertexProvider provider) {
                return provider.getGridVertex(world, pos, state);
            }
            return null;
        }, VivatechBlocks.WIRE_POST);
    }

    public static String createFileName(World world) {
        return world.getDimensionEntry().getIdAsString().replace(':', '_').replace('/', '_');
    }
}

package falseresync.vivatech.common.power;

import falseresync.vivatech.common.block.VivatechBlocks;
import falseresync.vivatech.common.blockentity.VivatechBlockEntities;
import falseresync.vivatech.common.power.ServerGridsLoader;
import falseresync.vivatech.common.power.WorldPowerSystem;
import falseresync.vivatech.common.power.grid.Appliance;
import falseresync.vivatech.common.power.grid.GridVertex;
import falseresync.vivatech.common.power.grid.GridVertexProvider;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import static falseresync.vivatech.common.Vivatech.vtId;

public class PowerSystem {
    public static final BlockApiLookup<Appliance, Direction> APPLIANCE = BlockApiLookup.get(vtId("appliance"), Appliance.class, Direction.class);
    public static final BlockApiLookup<GridVertex, Void> GRID_VERTEX = BlockApiLookup.get(vtId("grid_vertex"), GridVertex.class, Void.class);
    public static final LevelResource SAVE_PATH = new LevelResource("power_systems");
    public static final int DATA_VERSION = 100;
    private final MinecraftServer server;
    private final falseresync.vivatech.common.power.ServerGridsLoader serverGridsLoader;

    public PowerSystem(MinecraftServer server) {
        this.server = server;
        serverGridsLoader = new falseresync.vivatech.common.power.ServerGridsLoader(server);

        ServerWorldEvents.LOAD.register((_server, world) -> {
            serverGridsLoader.load(world);
        });

        ServerWorldEvents.UNLOAD.register((_server, world) -> {
            serverGridsLoader.save(world);
        });

        ServerTickEvents.START_WORLD_TICK.register(world -> {
            serverGridsLoader.tick(world);
        });
    }

    public static <T> Map<ChunkPos, T> createChunkPosKeyedMap() {
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

    public static String createFileName(Level world) {
        return world.dimensionTypeRegistration().getRegisteredName().replace(':', '_').replace('/', '_');
    }

    public ServerGridsLoader getServerGridsLoader() {
        return serverGridsLoader;
    }

    public WorldPowerSystem in(ResourceKey<Level> world) {
        return serverGridsLoader.getWorldGrids(world);
    }
}

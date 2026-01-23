package falseresync.vivatech.world.electricity;

import falseresync.vivatech.Vivatech;
import falseresync.vivatech.world.block.VivatechBlocks;
import falseresync.vivatech.world.blockentity.VivatechBlockEntities;
import falseresync.vivatech.world.electricity.grid.Appliance;
import falseresync.vivatech.world.electricity.grid.GridVertex;
import falseresync.vivatech.world.electricity.grid.GridVertexProvider;
import falseresync.vivatech.world.electricity.wire.WireType;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLevelEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
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


public class PowerSystemsManager {
    public static final BlockApiLookup<Appliance, Direction> APPLIANCES = BlockApiLookup.get(Vivatech.id("appliance"), Appliance.class, Direction.class);
    public static final BlockApiLookup<GridVertex, Void> GRID_VERTICES = BlockApiLookup.get(Vivatech.id("grid_vertex"), GridVertex.class, Void.class);
    public static final LevelResource SAVE_PATH = new LevelResource("power_systems");
    public static final int DATA_VERSION = 100;
    private final PowerSystemsLoader loader;

    public PowerSystemsManager(MinecraftServer server) {
        loader = new PowerSystemsLoader(server);

        ServerLevelEvents.LOAD.register((_server, level) -> {
            loader.load(level);
        });

        ServerLevelEvents.UNLOAD.register((_server, level) -> {
            loader.save(level);
        });

        ServerTickEvents.START_LEVEL_TICK.register(this::tick);
    }

    private void tick(Level level) {
        if (level.tickRateManager().runsNormally()) {
            var powerSystem = getFor(level.dimension());
            powerSystem.tick();
            powerSystem.syncWires();
        }
    }

    public static <T> Map<ChunkPos, T> createChunkPosKeyedMap() {
        return new Object2ObjectRBTreeMap<>(Comparator.comparingLong(ChunkPos::pack));
    }

    public static Set<ChunkPos> createChunkPosSet() {
        return new ObjectRBTreeSet<>(Comparator.comparingLong(ChunkPos::pack));
    }

    public static void init() {
        WireType.init();

        APPLIANCES.registerSelf(
                VivatechBlockEntities.GENERATOR,
                VivatechBlockEntities.HEATER,
                VivatechBlockEntities.STATIC_COMPENSATOR
        );

        GRID_VERTICES.registerForBlocks((level, pos, state, blockEntity, context) -> {
            if (state.getBlock() instanceof GridVertexProvider provider) {
                return provider.getGridVertex(level, pos, state);
            }
            return null;
        }, VivatechBlocks.WIRE_POST);
    }

    public static String createFileName(Level level) {
        return level.dimensionTypeRegistration().getRegisteredName().replace(':', '_').replace('/', '_');
    }

    public PowerSystemsLoader getLoader() {
        return loader;
    }

    public PowerSystem getFor(ResourceKey<Level> dimension) {
        return loader.getFor(dimension);
    }
}

package falseresync.vivatech.common.power;

import com.google.common.base.Predicates;
import com.mojang.serialization.Codec;
import falseresync.vivatech.network.s2c.WiresS2CPayload;
import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class GridsManager {
    public static final Codec<List<GridSnapshot>> CODEC = GridSnapshot.CODEC.listOf();
    private final ServerWorld world;
    private final Set<Grid> grids = new ReferenceOpenHashSet<>();
    /**
     * Tracks vertex positions, NOT appliances!
     */
    private final Map<BlockPos, Grid> gridLookup = new Object2ReferenceRBTreeMap<>();
    private final Map<ChunkPos, Set<Wire>> wires = PowerSystem.createChunkPosKeyedMap();
    private final Map<ChunkPos, Set<Wire>> addedWires = PowerSystem.createChunkPosKeyedMap();
    private final Map<ChunkPos, Set<Wire>> removedWires = PowerSystem.createChunkPosKeyedMap();
    private final List<ChunkPos> requestedChunks = new ObjectArrayList<>();

    public GridsManager(ServerWorld world) {
        this.world = world;
    }

    public void tick() {
        for (Grid grid : grids) {
            grid.tick();
        }
    }

    public void onWireAdded(Wire wire) {
        wires.computeIfAbsent(wire.chunkPos(), key -> new ObjectOpenHashSet<>()).add(wire);
        addedWires.computeIfAbsent(wire.chunkPos(), key -> new ObjectOpenHashSet<>()).add(wire);
    }

    public void onWireRemoved(Wire wire) {
        wires.computeIfAbsent(wire.chunkPos(), key -> new ObjectOpenHashSet<>()).remove(wire);
        removedWires.computeIfAbsent(wire.chunkPos(), key -> new ObjectOpenHashSet<>()).add(wire);
    }

    public void onWiresRequested(List<ChunkPos> requestedChunks) {
        this.requestedChunks.addAll(requestedChunks);
    }

    public void sendWires() {
        if (!requestedChunks.isEmpty()) {
            if (sendWires(wires, requestedChunks::contains, WiresS2CPayload.Added::new)) {
                requestedChunks.clear();
            }
        }

        if (!addedWires.isEmpty()) {
            if (sendWires(addedWires, Predicates.alwaysTrue(), WiresS2CPayload.Added::new)) {
                addedWires.clear();
            }
        }

        if (!removedWires.isEmpty()) {
            if (sendWires(removedWires, Predicates.alwaysTrue(), WiresS2CPayload.Removed::new)) {
                removedWires.clear();
            }
        }
    }

    public boolean sendWires(Map<ChunkPos, Set<Wire>> source, Predicate<ChunkPos> filter, Function<Set<Wire>, CustomPayload> payloadFactory) {
        if (source.isEmpty()) {
            return false;
        }

        var wiresForPlayerNetworkIds = new Int2ObjectRBTreeMap<Set<Wire>>();
        var playersForNetworkIds = new Int2ObjectRBTreeMap<ServerPlayerEntity>();
        for (var entry : source.entrySet()) {
            if (filter.test(entry.getKey())) {
                for (var player : PlayerLookup.tracking(world, entry.getKey())) {
                    playersForNetworkIds.put(player.getId(), player);
                    wiresForPlayerNetworkIds.merge(player.getId(), entry.getValue(), (a, b) -> {
                        a.addAll(b);
                        return a;
                    });
                }
            }
        }

        for (var entry : playersForNetworkIds.int2ObjectEntrySet()) {
            ServerPlayNetworking.send(entry.getValue(), payloadFactory.apply(wiresForPlayerNetworkIds.get(entry.getIntKey())));
        }

        return !playersForNetworkIds.isEmpty();
    }

    public Map<BlockPos, Grid> getGridLookup() {
        return gridLookup;
    }

    public Set<Grid> getGrids() {
        return grids;
    }

    public Grid findOrCreate(BlockPos u, BlockPos v, WireType wireType) {
        var found = find(u, v);
        if (found != null) {
            return found;
        }
        return create(wireType);
    }

    @Nullable
    public Grid find(BlockPos u, BlockPos v) {
        var gridU = gridLookup.get(u);
        if (gridU != null) {
            return gridU;
        }
        return gridLookup.get(v);
    }

    public Grid create(WireType wireType) {
        var grid = new Grid(this, world, wireType);
        grids.add(grid);
        return grid;
    }

    public void close() {
        grids.clear();
        gridLookup.clear();
        wires.clear();
        addedWires.clear();
        removedWires.clear();
        requestedChunks.clear();
    }
}

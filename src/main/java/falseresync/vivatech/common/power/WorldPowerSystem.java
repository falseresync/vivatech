package falseresync.vivatech.common.power;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import falseresync.vivatech.common.power.PowerSystem;
import falseresync.vivatech.common.power.grid.Grid;
import falseresync.vivatech.common.power.grid.GridSnapshot;
import falseresync.vivatech.common.power.wire.Wire;
import falseresync.vivatech.common.power.wire.WireType;
import falseresync.vivatech.network.s2c.WiresS2CPayload;
import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceRBTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

public class WorldPowerSystem {
    public static final Codec<List<GridSnapshot>> CODEC = GridSnapshot.CODEC.listOf();
    private final ServerLevel world;
    private final Set<Grid> grids = new ReferenceOpenHashSet<>();
    /**
     * Tracks vertex positions, NOT appliances!
     */
    private final Map<BlockPos, Grid> gridLookup = new Object2ReferenceRBTreeMap<>();
    private final Map<ChunkPos, Set<Wire>> wires = falseresync.vivatech.common.power.PowerSystem.createChunkPosKeyedMap();
    private final Map<ChunkPos, Set<Wire>> addedWires = falseresync.vivatech.common.power.PowerSystem.createChunkPosKeyedMap();
    private final Map<ChunkPos, Set<Wire>> removedWires = PowerSystem.createChunkPosKeyedMap();
    private final List<ChunkPos> requestedChunks = new ObjectArrayList<>();

    public WorldPowerSystem(ServerLevel world) {
        this.world = world;
    }

    public void tick() {
        for (Grid grid : grids) {
            grid.tick();
        }
    }

    public void addWire(Wire wire) {
        wires.computeIfAbsent(wire.chunkPos(), key -> new ObjectOpenHashSet<>()).add(wire);
        addedWires.computeIfAbsent(wire.chunkPos(), key -> new ObjectOpenHashSet<>()).add(wire);
    }

    public void removeWire(Wire wire) {
        wires.computeIfAbsent(wire.chunkPos(), key -> new ObjectOpenHashSet<>()).remove(wire);
        removedWires.computeIfAbsent(wire.chunkPos(), key -> new ObjectOpenHashSet<>()).add(wire);
    }

    public void queueRequestedChunks(List<ChunkPos> requestedChunks) {
        this.requestedChunks.addAll(requestedChunks);
    }

    public void syncWires() {
        if (!requestedChunks.isEmpty()) {
            if (syncWires(wires, requestedChunks::contains, WiresS2CPayload.Added::new)) {
                requestedChunks.clear();
            }
        }

        if (!addedWires.isEmpty()) {
            if (syncWires(addedWires, Predicates.alwaysTrue(), WiresS2CPayload.Added::new)) {
                addedWires.clear();
            }
        }

        if (!removedWires.isEmpty()) {
            if (syncWires(removedWires, Predicates.alwaysTrue(), WiresS2CPayload.Removed::new)) {
                removedWires.clear();
            }
        }
    }

    public boolean syncWires(Map<ChunkPos, Set<Wire>> source, Predicate<ChunkPos> filter, Function<Set<Wire>, CustomPacketPayload> payloadFactory) {
        if (source.isEmpty()) {
            return false;
        }

        var wiresForPlayerNetworkIds = new Int2ObjectRBTreeMap<Set<Wire>>();
        var playersForNetworkIds = new Int2ObjectRBTreeMap<ServerPlayer>();
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

    public List<GridSnapshot> createSnapshots() {
        grids.removeIf(Grid::isEmpty);
        var list = new ImmutableList.Builder<GridSnapshot>();
        for (var grid : grids) {
            list.add(grid.createSnapshot());
        }
        return list.build();
    }

    public boolean add(Grid grid) {
        return grids.add(grid);
    }

    public boolean remove(Grid grid) {
        return grids.remove(grid);
    }

    public int count() {
        grids.removeIf(Grid::isEmpty);
        return grids.size();
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
}

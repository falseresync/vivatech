package falseresync.vivatech.client.wire;

import falseresync.vivatech.network.serverbound.RequestWiresServerboundPayload;
import falseresync.vivatech.world.electricity.wire.Wire;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLevelEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class WiresManager {
    private final Minecraft client;
    private final Map<ResourceKey<Level>, Tracker> trackers = new Object2ObjectArrayMap<>();

    public WiresManager(Minecraft client) {
        this.client = client;

        ClientLevelEvents.AFTER_CLIENT_LEVEL_CHANGE.register((_ignored, world) -> {
            trackers.putIfAbsent(world.dimension(), new Tracker());
        });

        ClientChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
            trackers.get(world.dimension()).unsyncedChunks.add(chunk.getPos());
        });

        ClientChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> {
            trackers.get(world.dimension()).wires.removeIf(wire -> wire.chunkPos().equals(chunk.getPos()));
        });

        ClientTickEvents.START_LEVEL_TICK.register(_ -> tick());
    }

    public void add(ResourceKey<Level> world, Set<Wire> wires) {
        trackers.get(world).wires.addAll(wires);
    }

    public void remove(ResourceKey<Level> world, Set<Wire> wires) {
        trackers.get(world).wires.removeAll(wires);
    }

    public Set<Wire> getFor(ResourceKey<Level> dimension) {
        return trackers.get(dimension).wires;
    }

    public void tick() {
        if (client.player == null) {
            trackers.values().forEach(Tracker::clear);
            return;
        }

        trackers.values().forEach(Tracker::sync);
    }

    public static class Tracker {
        private final Set<Wire> wires = new ObjectOpenHashSet<>();
        private final List<ChunkPos> unsyncedChunks = new ObjectArrayList<>();

        public void sync() {
            if (!unsyncedChunks.isEmpty()) {
                ClientPlayNetworking.send(new RequestWiresServerboundPayload(List.copyOf(unsyncedChunks)));
                unsyncedChunks.clear();
            }
        }

        public void clear() {
            wires.clear();
            unsyncedChunks.clear();
        }
    }
}

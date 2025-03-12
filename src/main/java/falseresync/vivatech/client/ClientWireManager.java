package falseresync.vivatech.client;

import falseresync.vivatech.common.power.Wire;
import falseresync.vivatech.network.c2s.RequestWiresC2SPayload;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ClientWireManager {
    private final MinecraftClient client;
    private final Map<RegistryKey<World>, Tracker> trackers = new Object2ObjectArrayMap<>();

    public ClientWireManager(MinecraftClient client) {
        this.client = client;

        ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register((_ignored, world) -> {
            trackers.putIfAbsent(world.getRegistryKey(), new Tracker());
        });

        ClientChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
            trackers.get(world.getRegistryKey()).unsyncedChunks.add(chunk.getPos());
        });

        ClientChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> {
            trackers.get(world.getRegistryKey()).wires.removeIf(wire -> wire.chunkPos().equals(chunk.getPos()));
        });
    }

    public void onWiresAdded(RegistryKey<World> world, Set<Wire> wires) {
        trackers.get(world).wires.addAll(wires);
    }

    public void onWiresRemoved(RegistryKey<World> world, Set<Wire> wires) {
        trackers.get(world).wires.removeAll(wires);
    }

    public Set<Wire> getWires(RegistryKey<World> world) {
        return trackers.get(world).wires;
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
                ClientPlayNetworking.send(new RequestWiresC2SPayload(List.copyOf(unsyncedChunks)));
                unsyncedChunks.clear();
            }
        }

        public void clear() {
            wires.clear();
            unsyncedChunks.clear();
        }
    }
}

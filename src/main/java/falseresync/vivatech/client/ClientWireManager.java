package falseresync.vivatech.client;

import falseresync.vivatech.common.power.Wire;
import falseresync.vivatech.network.c2s.RequestWiresC2SPayload;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.ChunkPos;

import java.util.List;
import java.util.Set;

public class ClientWireManager {
    private final MinecraftClient client;
    private final Set<Wire> wires = new ObjectOpenHashSet<>();
    private final List<ChunkPos> unsyncedChunks = new ObjectArrayList<>();

    public ClientWireManager(MinecraftClient client) {
        this.client = client;
    }

    public void onWiresAdded(Set<Wire> wires) {
        this.wires.addAll(wires);
    }

    public void onWiresRemoved(Set<Wire> wires) {
        this.wires.removeAll(wires);
    }

    public Set<Wire> getWires() {
        return wires;
    }

    public void queueUnsyncedChunk(ChunkPos pos) {
        unsyncedChunks.add(pos);
    }

    public void tick() {
        if (client.player == null) {
            clear();
            return;
        }

        if (!unsyncedChunks.isEmpty()) {
            ClientPlayNetworking.send(new RequestWiresC2SPayload(List.copyOf(unsyncedChunks)));
            unsyncedChunks.clear();
        }

        wires.removeIf(wire ->
                !client.player.getPos().isInRange(wire.middle(), client.options.getClampedViewDistance() * 16));
    }

    public void clear() {
        wires.clear();
    }
}

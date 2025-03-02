package falseresync.vivatech.common.power;

import com.google.common.base.Predicates;
import com.mojang.serialization.Codec;
import falseresync.vivatech.common.blockentity.VtBlockEntities;
import falseresync.vivatech.network.s2c.WiresPayload;
import it.unimi.dsi.fastutil.ints.Int2ObjectRBTreeMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.PathUtil;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import static falseresync.vivatech.common.Vivatech.LOGGER;
import static falseresync.vivatech.common.Vivatech.vtId;

public class PowerSystemsManager {
    public static final BlockApiLookup<PowerNode, Void> POWER_NODE =
            BlockApiLookup.get(vtId("power_node"), PowerNode.class, Void.class);
    public static final WorldSavePath SAVE_PATH = new WorldSavePath("power_systems");
    public static final int DATA_VERSION = 100;
    public static final Codec<List<PowerSystemSnapshot>> CODEC = PowerSystemSnapshot.CODEC.listOf();
    private final Map<RegistryKey<World>, Set<PowerSystem>> powerSystems = new Object2ObjectArrayMap<>();
    private final Map<RegistryKey<World>, Map<ChunkPos, Set<Wire>>> unsyncedWires = new Object2ObjectArrayMap<>();
    private final Map<RegistryKey<World>, Map<ChunkPos, Set<Wire>>> renderableWires = new Object2ObjectArrayMap<>();
    private final Map<RegistryKey<World>, List<ChunkPos>> requestedChunks = new Object2ObjectArrayMap<>();
    private final MinecraftServer server;

    public PowerSystemsManager(MinecraftServer server) {
        this.server = server;
        load();
    }

    public static void registerAll() {
        POWER_NODE.registerSelf(VtBlockEntities.WIRE_POST);
    }

    public Set<PowerSystem> getAll(World world) {
        return powerSystems.get(world.getRegistryKey());
    }

    public PowerSystem create(World world) {
        var powerSystem = new PowerSystem(this, world);
        powerSystems.computeIfPresent(world.getRegistryKey(), (worldRegistryKey, systemsInWorld) -> {
            systemsInWorld.add(powerSystem);
            return systemsInWorld;
        });

        return powerSystem;
    }

    public void queueWiresRequest(ServerWorld world, List<ChunkPos> chunks) {
        requestedChunks.merge(world.getRegistryKey(), chunks, (a, b) -> {
            a.addAll(b);
            return a;
        });
    }

    public void sendRequestedWires() {
        for (var world : server.getWorlds()) {
            var requestedChunksInWorld = requestedChunks.get(world.getRegistryKey());
            if (requestedChunksInWorld.isEmpty()) {
                return;
            }

            var sentAnything = sendWires(world, renderableWires.get(world.getRegistryKey()), requestedChunksInWorld::contains);
            if (sentAnything) {
                requestedChunksInWorld.clear();
            }
        }
    }

    public void sendUnsyncedWires() {
        collectUnsyncedWires();
        for (var world : server.getWorlds()) {
            var wiresInWorld = unsyncedWires.get(world.getRegistryKey());
            var sentAnything = sendWires(world, wiresInWorld, Predicates.alwaysTrue());
            if (sentAnything) {
                wiresInWorld.clear();
            }
        }
    }

    private boolean sendWires(ServerWorld world, Map<ChunkPos, Set<Wire>> source, Predicate<ChunkPos> predicate) {
        if (source.isEmpty()) {
            return false;
        }

        var wiresForPlayerNetworkIds = new Int2ObjectRBTreeMap<Set<Wire>>();
        var playersForNetworkIds = new Int2ObjectRBTreeMap<ServerPlayerEntity>();
        for (var entry : source.entrySet()) {
            if (predicate.test(entry.getKey())) {
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
            ServerPlayNetworking.send(entry.getValue(), new WiresPayload(wiresForPlayerNetworkIds.get(entry.getIntKey())));
        }

        return !playersForNetworkIds.isEmpty();
    }

    private void collectUnsyncedWires() {
        powerSystems.forEach((key, systemsInWorld) -> {
            for (var powerSystem : systemsInWorld) {
                if (powerSystem.requiresSyncing()) {
                    var currentUnsyncedWires = powerSystem.getUnsyncedWires();
                    collectWires(key, currentUnsyncedWires, unsyncedWires);
                    collectWires(key, currentUnsyncedWires, renderableWires);
                    renderableWires.get(key).forEach((pos, wires) -> wires.removeIf(Wire::removed));
                    powerSystem.markSynced();
                }
            }
        });
    }

    private void collectWires(RegistryKey<World> key, Map<ChunkPos, Set<Wire>> current, Map<RegistryKey<World>, Map<ChunkPos, Set<Wire>>> destination) {
        destination.computeIfPresent(key, (_ignored, wiresInWorld) -> {
            for (var entry : current.entrySet()) {
                wiresInWorld.merge(entry.getKey(), entry.getValue(), (a, b) -> {
                    a.addAll(b);
                    return a;
                });
            }
            return wiresInWorld;
        });
    }

    public void load() {
        for (var world : server.getWorlds()) {
            var powerSystemsSnapshots = loadSnapshots(world);
            powerSystems.put(world.getRegistryKey(), new ObjectOpenHashSet<>(100));
            unsyncedWires.put(world.getRegistryKey(), PowerUtil.createWireMap());
            renderableWires.put(world.getRegistryKey(), PowerUtil.createWireMap());
            requestedChunks.put(world.getRegistryKey(), new ObjectArrayList<>());
            for (var snapshot : powerSystemsSnapshots) {
                powerSystems.computeIfPresent(world.getRegistryKey(), (key, systemsInWorld) -> {
                    systemsInWorld.add(snapshot.reconstruct(this, world));
                    return systemsInWorld;
                });
            }
        }
    }

    private List<PowerSystemSnapshot> loadSnapshots(World world) {
        var filePath = server.getSavePath(SAVE_PATH).resolve(createFileName(world) + ".nbt");
        if (Files.isRegularFile(filePath)) {
            try (var fileInputStream = Files.newInputStream(filePath)) {
                try (var dataInputStream = new DataInputStream(fileInputStream)) {
                    var nbt = NbtIo.readCompound(dataInputStream, NbtSizeTracker.ofUnlimitedBytes());
                    return CODEC.parse(NbtOps.INSTANCE, nbt.get("data")).getOrThrow();
                } catch (Throwable eOuter) {
                    try {
                        fileInputStream.close();
                    } catch (Throwable eInner) {
                        eOuter.addSuppressed(eInner);
                    }

                    throw eOuter;
                }
            } catch (IOException e) {
                LOGGER.error("Couldn't access power systems in {}", filePath, e);
            } catch (IllegalStateException e) {
                LOGGER.error("Couldn't parse power systems in {}", filePath, e);
            }
        }

        return List.of();
    }

    public void save() {
        for (var world : server.getWorlds()) {
            var snapshots = powerSystems.get(world.getRegistryKey()).stream().map(PowerSystem::createSnapshot).toList();
            var filePath = server.getSavePath(SAVE_PATH).resolve(createFileName(world) + ".nbt");
            try {
                PathUtil.createDirectories(filePath.getParent());
                var fileOutputStream = Files.newOutputStream(filePath);

                try (var dataOutput = new DataOutputStream(fileOutputStream)) {
                    var nbt = new NbtCompound();
                    nbt.putInt("data_version", DATA_VERSION);
                    nbt.put("data", CODEC.encodeStart(NbtOps.INSTANCE, snapshots).getOrThrow());
                    NbtIo.writeCompound(nbt, dataOutput);
                } catch (Throwable eOuter) {
                    try {
                        fileOutputStream.close();
                    } catch (Throwable eInner) {
                        eOuter.addSuppressed(eInner);
                    }

                    throw eOuter;
                }

                fileOutputStream.close();
            } catch (IOException e) {
                LOGGER.error("Couldn't save power systems to {}", filePath, e);
            } catch (IllegalStateException e) {
                LOGGER.error("Couldn't serialize power systems for {}", filePath, e);
            }
        }
    }

    private static String createFileName(World world) {
        return world.getDimensionEntry().getIdAsString().replace(':', '_').replace('/', '_');
    }
}

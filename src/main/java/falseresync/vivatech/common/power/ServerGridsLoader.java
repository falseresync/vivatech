package falseresync.vivatech.common.power;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.PathUtil;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static falseresync.vivatech.common.Vivatech.LOGGER;

public class ServerGridsLoader {
    private final Map<RegistryKey<World>, GridsManager> gridsManagers = new Object2ObjectArrayMap<>();
    private final MinecraftServer server;

    public ServerGridsLoader(MinecraftServer server) {
        this.server = server;
    }

    public void tick(World world) {
        if (world.getTickManager().shouldTick()) {
            getGridsManager(world).tick();
        }
        sendWires();
    }

    public GridsManager getGridsManager(World world) {
        return gridsManagers.get(world.getRegistryKey());
    }

    public void onWiresRequested(RegistryKey<World> key, List<ChunkPos> requestedChunks) {
        gridsManagers.get(key).onWiresRequested(requestedChunks);
    }

    public void sendWires() {
        for (var gridsManager : gridsManagers.values()) {
            gridsManager.sendWires();
        }
    }

    public void load(ServerWorld world) {
        var gridsManager = new GridsManager(world);
        for (var snapshot : loadSnapshots(world)) {
            gridsManager.getGrids().add(snapshot.reconstruct(gridsManager, world));
        }
        gridsManagers.put(world.getRegistryKey(), gridsManager);
    }

    private List<GridSnapshot> loadSnapshots(World world) {
        var filePath = server.getSavePath(PowerSystem.SAVE_PATH).resolve(PowerSystem.createFileName(world) + ".nbt");
        if (Files.isRegularFile(filePath)) {
            try (var fileInputStream = Files.newInputStream(filePath)) {
                try (var dataInputStream = new DataInputStream(fileInputStream)) {
                    var nbt = NbtIo.readCompound(dataInputStream, NbtSizeTracker.ofUnlimitedBytes());
                    return GridsManager.CODEC.parse(NbtOps.INSTANCE, nbt.get("grids")).getOrThrow();
                } catch (Throwable eOuter) {
                    try {
                        fileInputStream.close();
                    } catch (Throwable eInner) {
                        eOuter.addSuppressed(eInner);
                    }

                    throw eOuter;
                }
            } catch (IOException e) {
                LOGGER.error("Couldn't access power systems in %s".formatted(filePath));
                LOGGER.error(e);
            } catch (IllegalStateException e) {
                LOGGER.error("Couldn't parse power systems in %s".formatted(filePath));
                LOGGER.error(e);
            }
        }

        return List.of();
    }

    public void save(ServerWorld world) {
        var snapshots = gridsManagers.get(world.getRegistryKey()).getGrids().stream()
                .filter(grid -> !grid.isEmpty())
                .map(Grid::createSnapshot)
                .toList();
        var filePath = server.getSavePath(PowerSystem.SAVE_PATH).resolve(PowerSystem.createFileName(world) + ".nbt");
        try {
            PathUtil.createDirectories(filePath.getParent());
            var fileOutputStream = Files.newOutputStream(filePath);

            try (var dataOutput = new DataOutputStream(fileOutputStream)) {
                var nbt = new NbtCompound();
                nbt.putInt("data_version", PowerSystem.DATA_VERSION);
                nbt.put("grids", GridsManager.CODEC.encodeStart(NbtOps.INSTANCE, snapshots).getOrThrow());
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
            LOGGER.error("Couldn't save power systems to %s".formatted(filePath));
            LOGGER.error(e);
        } catch (IllegalStateException e) {
            LOGGER.error("Couldn't serialize power systems for %s".formatted(filePath));
            LOGGER.error(e);
        }
    }

}

package falseresync.vivatech.world.electricity;

import falseresync.vivatech.world.electricity.grid.GridSnapshot;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.FileUtil;
import net.minecraft.world.level.Level;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static falseresync.vivatech.Vivatech.LOGGER;


public class PowerSystemsLoader {
    private final Map<ResourceKey<Level>, PowerSystem> powerSystemsInDimensions = new Object2ObjectArrayMap<>();
    private final MinecraftServer server;

    public PowerSystemsLoader(MinecraftServer server) {
        this.server = server;
    }

    public PowerSystem getFor(ResourceKey<Level> dimension) {
        return powerSystemsInDimensions.get(dimension);
    }

    public void load(ServerLevel world) {
        var worldGrids = new PowerSystem(world);
        for (var snapshot : loadSnapshots(world)) {
            worldGrids.add(snapshot.reconstruct(worldGrids, world));
        }
        powerSystemsInDimensions.put(world.dimension(), worldGrids);

        LOGGER.info("Loaded %s grids in %s".formatted(worldGrids.count(), world.dimension().identifier()));
    }

    private List<GridSnapshot> loadSnapshots(Level world) {
        var filePath = server.getWorldPath(PowerSystemsManager.SAVE_PATH).resolve(PowerSystemsManager.createFileName(world) + ".nbt");
        if (Files.isRegularFile(filePath)) {
            try (var fileInputStream = Files.newInputStream(filePath)) {
                try (var dataInputStream = new DataInputStream(fileInputStream)) {
                    var nbt = NbtIo.read(dataInputStream, NbtAccounter.unlimitedHeap());
                    return PowerSystem.CODEC.parse(NbtOps.INSTANCE, nbt.get("grids")).getOrThrow();
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

    public void save(ServerLevel world) {
        var snapshots = getFor(world.dimension()).createSnapshots();
        var filePath = server.getWorldPath(PowerSystemsManager.SAVE_PATH).resolve(PowerSystemsManager.createFileName(world) + ".nbt");
        try {
            FileUtil.createDirectoriesSafe(filePath.getParent());
            var fileOutputStream = Files.newOutputStream(filePath);

            try (var dataOutput = new DataOutputStream(fileOutputStream)) {
                var nbt = new CompoundTag();
                nbt.putInt("data_version", PowerSystemsManager.DATA_VERSION);
                nbt.put("grids", PowerSystem.CODEC.encodeStart(NbtOps.INSTANCE, snapshots).getOrThrow());
                NbtIo.write(nbt, dataOutput);
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

        LOGGER.info("Saved %s grids in %s".formatted(snapshots.size(), world.dimension().identifier()));
    }
}

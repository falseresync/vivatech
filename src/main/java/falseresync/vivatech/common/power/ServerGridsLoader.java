package falseresync.vivatech.common.power;

import falseresync.vivatech.common.power.PowerSystem;
import falseresync.vivatech.common.power.WorldPowerSystem;
import falseresync.vivatech.common.power.grid.GridSnapshot;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.FileUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import static falseresync.vivatech.common.Vivatech.LOGGER;

public class ServerGridsLoader {
    private final Map<ResourceKey<Level>, falseresync.vivatech.common.power.WorldPowerSystem> worldGridsForWorld = new Object2ObjectArrayMap<>();
    private final MinecraftServer server;

    public ServerGridsLoader(MinecraftServer server) {
        this.server = server;
    }

    public void tick(Level world) {
        if (world.tickRateManager().runsNormally()) {
            var worldGrids = getWorldGrids(world.dimension());
            worldGrids.tick();
            worldGrids.syncWires();
        }
    }

    public falseresync.vivatech.common.power.WorldPowerSystem getWorldGrids(ResourceKey<Level> world) {
        return worldGridsForWorld.get(world);
    }

    public void load(ServerLevel world) {
        var worldGrids = new falseresync.vivatech.common.power.WorldPowerSystem(world);
        for (var snapshot : loadSnapshots(world)) {
            worldGrids.add(snapshot.reconstruct(worldGrids, world));
        }
        worldGridsForWorld.put(world.dimension(), worldGrids);

        LOGGER.info("Loaded %s grids in %s".formatted(worldGrids.count(), world.dimension().location()));
    }

    private List<GridSnapshot> loadSnapshots(Level world) {
        var filePath = server.getWorldPath(falseresync.vivatech.common.power.PowerSystem.SAVE_PATH).resolve(falseresync.vivatech.common.power.PowerSystem.createFileName(world) + ".nbt");
        if (Files.isRegularFile(filePath)) {
            try (var fileInputStream = Files.newInputStream(filePath)) {
                try (var dataInputStream = new DataInputStream(fileInputStream)) {
                    var nbt = NbtIo.read(dataInputStream, NbtAccounter.unlimitedHeap());
                    return falseresync.vivatech.common.power.WorldPowerSystem.CODEC.parse(NbtOps.INSTANCE, nbt.get("grids")).getOrThrow();
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
        var snapshots = getWorldGrids(world.dimension()).createSnapshots();
        var filePath = server.getWorldPath(falseresync.vivatech.common.power.PowerSystem.SAVE_PATH).resolve(falseresync.vivatech.common.power.PowerSystem.createFileName(world) + ".nbt");
        try {
            FileUtil.createDirectoriesSafe(filePath.getParent());
            var fileOutputStream = Files.newOutputStream(filePath);

            try (var dataOutput = new DataOutputStream(fileOutputStream)) {
                var nbt = new CompoundTag();
                nbt.putInt("data_version", PowerSystem.DATA_VERSION);
                nbt.put("grids", WorldPowerSystem.CODEC.encodeStart(NbtOps.INSTANCE, snapshots).getOrThrow());
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

        LOGGER.info("Saved %s grids in %s".formatted(snapshots.size(), world.dimension().location()));
    }
}

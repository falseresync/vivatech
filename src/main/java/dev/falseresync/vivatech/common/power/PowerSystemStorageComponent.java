package dev.falseresync.vivatech.common.power;

import dev.falseresync.vivatech.api.power.PowerGridNode;
import dev.falseresync.vivatech.api.power.PowerSystem;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PowerSystemStorageComponent implements AutoSyncedComponent {
    protected final World world;
    protected Set<VivatechPowerGrid> powerGrids = new ObjectOpenHashSet<>();

    public PowerSystemStorageComponent(World world) {
        this.world = world;
    }

    private static BlockPos castPos(NbtElement posElement) {
        return BlockPos.fromLong(((NbtLong) posElement).longValue());
    }

    @Override
    public void readFromNbt(NbtCompound tag) {
        var grids = tag.getList("grids", NbtElement.COMPOUND_TYPE)
                .stream()
                .map(gridElement -> ((NbtCompound) gridElement).getList("nodes", NbtElement.LONG_TYPE)
                        .stream()
                        .flatMap(posElement -> Stream.ofNullable(findNode(castPos(posElement))))
                        .collect(Collectors.toUnmodifiableSet()))
                .map(VivatechPowerGrid::new)
                .collect(Collectors.toUnmodifiableSet());
        powerGrids.addAll(grids);
    }

    @Override
    public void writeToNbt(NbtCompound tag) {
        var grids = powerGrids.stream()
                .map(grid -> {
                    var nodes = grid.getNodes().stream()
                            .map(node -> NbtLong.of(node.getPos().asLong()))
                            .collect(Collectors.toCollection(NbtList::new));
                    var gridTag = new NbtCompound();
                    gridTag.put("nodes", nodes);
                    return gridTag;
                })
                .collect(Collectors.toCollection(NbtList::new));
        tag.put("grids", grids);
    }

    @Nullable
    private PowerGridNode findNode(BlockPos pos) {
        return PowerSystem.NODE_LOOKUP.find(world, pos, null);
    }
}

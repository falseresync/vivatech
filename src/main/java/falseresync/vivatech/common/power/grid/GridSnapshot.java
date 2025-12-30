package falseresync.vivatech.common.power.grid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import falseresync.vivatech.common.power.WorldPowerSystem;
import falseresync.vivatech.common.power.wire.WireType;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import net.minecraft.server.level.ServerLevel;

public record GridSnapshot(WireType wireType, Set<falseresync.vivatech.common.power.grid.GridEdge> edges) {
    public static final Codec<GridSnapshot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            WireType.REGISTRY.byNameCodec().fieldOf("wire_type").forGetter(GridSnapshot::wireType),
            falseresync.vivatech.common.power.grid.GridEdge.CODEC.listOf()
                    .xmap(it -> (Set<GridEdge>) new ObjectOpenHashSet<>(it), it -> it.stream().toList())
                    .fieldOf("edges")
                    .forGetter(GridSnapshot::edges)
    ).apply(instance, GridSnapshot::new));

    public falseresync.vivatech.common.power.grid.Grid reconstruct(WorldPowerSystem worldPowerSystem, ServerLevel world) {
        return new Grid(worldPowerSystem, world, wireType, edges);
    }
}

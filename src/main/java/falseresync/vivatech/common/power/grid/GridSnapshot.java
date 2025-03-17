package falseresync.vivatech.common.power.grid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import falseresync.vivatech.common.power.WorldPowerSystem;
import falseresync.vivatech.common.power.wire.WireType;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.server.world.ServerWorld;

import java.util.Set;

public record GridSnapshot(WireType wireType, Set<GridEdge> edges) {
    public static final Codec<GridSnapshot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            WireType.REGISTRY.getCodec().fieldOf("wire_type").forGetter(GridSnapshot::wireType),
            GridEdge.CODEC.listOf()
                    .xmap(it -> (Set<GridEdge>) new ObjectOpenHashSet<>(it), it -> it.stream().toList())
                    .fieldOf("edges")
                    .forGetter(GridSnapshot::edges)
    ).apply(instance, GridSnapshot::new));

    public Grid reconstruct(WorldPowerSystem worldPowerSystem, ServerWorld world) {
        return new Grid(worldPowerSystem, world, wireType, edges);
    }
}

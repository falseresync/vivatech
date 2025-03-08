package falseresync.vivatech.common.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

    public Grid reconstruct(GridsManager gridsManager, ServerWorld world) {
        return new Grid(gridsManager, world, wireType, edges);
    }
}

package falseresync.vivatech.world.electricity.grid;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import falseresync.vivatech.world.electricity.PowerSystem;
import falseresync.vivatech.world.electricity.wire.WireType;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.server.level.ServerLevel;

import java.util.Set;

public record GridSnapshot(WireType wireType, Set<GridEdge> edges) {
    public static final Codec<GridSnapshot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            WireType.REGISTRY.byNameCodec().fieldOf("wire_type").forGetter(GridSnapshot::wireType),
            GridEdge.CODEC.listOf()
                    .xmap(it -> (Set<GridEdge>) new ObjectOpenHashSet<>(it), it -> it.stream().toList())
                    .fieldOf("edges")
                    .forGetter(GridSnapshot::edges)
    ).apply(instance, GridSnapshot::new));

    public Grid reconstruct(PowerSystem powerSystem, ServerLevel world) {
        return new Grid(powerSystem, world, wireType, edges);
    }
}

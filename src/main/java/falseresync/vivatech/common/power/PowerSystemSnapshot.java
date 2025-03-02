package falseresync.vivatech.common.power;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.world.World;

import java.util.Set;

public record PowerSystemSnapshot(Set<Wire> wires) {
    public static final Codec<PowerSystemSnapshot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Wire.CODEC.listOf()
                    .xmap(it -> (Set<Wire>) new ObjectOpenHashSet<>(it), it -> it.stream().toList())
                    .fieldOf("wires")
                    .forGetter(PowerSystemSnapshot::wires)
    ).apply(instance, PowerSystemSnapshot::new));

    public PowerSystem reconstruct(PowerSystemsManager powerSystemsManager, World world) {
        return new PowerSystem(powerSystemsManager, world, wires);
    }
}

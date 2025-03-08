package falseresync.vivatech.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.dynamic.Codecs;

public record ItemBarComponent(int step, int color) {
    public static final ItemBarComponent DEFAULT = new ItemBarComponent(0, 0);
    public static final Codec<ItemBarComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(0, 13).fieldOf("step").forGetter(ItemBarComponent::step),
            Codecs.ARGB.fieldOf("color").forGetter(ItemBarComponent::color)
    ).apply(instance, ItemBarComponent::new));
    public static final PacketCodec<RegistryByteBuf, ItemBarComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, ItemBarComponent::step,
            PacketCodecs.INTEGER, ItemBarComponent::color,
            ItemBarComponent::new
    );
}

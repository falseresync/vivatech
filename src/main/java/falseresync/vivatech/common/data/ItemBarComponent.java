package falseresync.vivatech.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;

public record ItemBarComponent(int step, int color) {
    public static final ItemBarComponent DEFAULT = new ItemBarComponent(0, -1);
    public static final Codec<ItemBarComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.intRange(0, 13).fieldOf("step").forGetter(ItemBarComponent::step),
            ExtraCodecs.ARGB_COLOR_CODEC.fieldOf("color").forGetter(ItemBarComponent::color)
    ).apply(instance, ItemBarComponent::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemBarComponent> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ItemBarComponent::step,
            ByteBufCodecs.INT, ItemBarComponent::color,
            ItemBarComponent::new
    );
}

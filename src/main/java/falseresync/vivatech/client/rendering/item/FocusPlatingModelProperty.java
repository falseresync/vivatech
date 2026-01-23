package falseresync.vivatech.client.rendering.item;

import com.mojang.serialization.MapCodec;
import falseresync.vivatech.common.data.VivatechComponents;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record FocusPlatingModelProperty() implements RangeSelectItemModelProperty {
    public static final MapCodec<FocusPlatingModelProperty> MAP_CODEC = MapCodec.unit(new FocusPlatingModelProperty());

    @Override
    public float get(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable ItemOwner itemOwner, int i) {
        return itemStack.getOrDefault(VivatechComponents.FOCUS_PLATING, -1);
    }

    @Override
    public MapCodec<FocusPlatingModelProperty> type() {
        return MAP_CODEC;
    }
}

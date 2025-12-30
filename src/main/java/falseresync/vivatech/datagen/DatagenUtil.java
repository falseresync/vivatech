package falseresync.vivatech.datagen;

import falseresync.vivatech.common.item.focus.FocusPlating;
import net.minecraft.resources.ResourceLocation;

public class DatagenUtil {
    public static ResourceLocation suffixPlating(ResourceLocation id, FocusPlating plating) {
        return id.withSuffix("_plating_" + plating.name().toLowerCase());
    }
}

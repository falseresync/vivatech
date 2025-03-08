package falseresync.vivatech.datagen;

import falseresync.vivatech.common.item.focus.FocusPlating;
import net.minecraft.util.Identifier;

public class DatagenUtil {
    public static Identifier suffixPlating(Identifier id, FocusPlating plating) {
        return id.withSuffixedPath("_plating_" + plating.name().toLowerCase());
    }
}

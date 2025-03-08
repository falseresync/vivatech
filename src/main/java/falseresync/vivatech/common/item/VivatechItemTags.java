package falseresync.vivatech.common.item;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import static falseresync.vivatech.common.Vivatech.vtId;

public class VivatechItemTags {
    public static final TagKey<Item> FOCUSES = TagKey.of(RegistryKeys.ITEM, vtId("focuses"));
    public static final TagKey<Item> WANDS = TagKey.of(RegistryKeys.ITEM, vtId("wands"));

    public static void init() {
    }
}

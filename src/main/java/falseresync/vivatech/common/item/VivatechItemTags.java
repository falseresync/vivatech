package falseresync.vivatech.common.item;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import static falseresync.vivatech.common.Vivatech.vtId;

public class VivatechItemTags {
    public static final TagKey<Item> FOCUSES = TagKey.of(RegistryKeys.ITEM, vtId("focuses"));
    public static final TagKey<Item> GADGETS = TagKey.of(RegistryKeys.ITEM, vtId("gadgets"));
    public static final TagKey<Item> CHARGEABLE = TagKey.of(RegistryKeys.ITEM, vtId("chargeable"));

    public static void init() {
    }
}

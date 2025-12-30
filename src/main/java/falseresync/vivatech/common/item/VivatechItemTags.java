package falseresync.vivatech.common.item;

import static falseresync.vivatech.common.Vivatech.vtId;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class VivatechItemTags {
    public static final TagKey<Item> FOCUSES = TagKey.create(Registries.ITEM, vtId("focuses"));
    public static final TagKey<Item> GADGETS = TagKey.create(Registries.ITEM, vtId("gadgets"));
    public static final TagKey<Item> CHARGEABLE = TagKey.create(Registries.ITEM, vtId("chargeable"));

    public static void init() {
    }
}

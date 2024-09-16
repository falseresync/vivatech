package falseresync.vivatech.item;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

import static falseresync.vivatech.Vivatech.vtId;

public final class VtItems {
    public static final Item RAW_ZINC =
            r("raw_zinc", new Item(new Item.Settings().rarity(Rarity.UNCOMMON)));
    public static final Item ZINC_INGOT =
            r("zinc_ingot", new Item(new Item.Settings().rarity(Rarity.UNCOMMON)));
    public static final Item ZINC_NUGGET =
            r("zinc_nugget", new Item(new Item.Settings().rarity(Rarity.UNCOMMON)));
    public static final SacrificialDaggerItem SACRIFICIAL_DAGGER =
            r("sacrificial_dagger", new SacrificialDaggerItem(
                    VtToolMaterials.ZINC,
                    new Item.Settings().maxCount(1).maxDamage(256).rarity(Rarity.UNCOMMON)
                            .attributeModifiers(SacrificialDaggerItem.createAttributeModifiers(
                                    VtToolMaterials.ZINC, 2, -2.0f))));

    private static <T extends Item> T r(String id, T item) {
        Registry.register(Registries.ITEM, vtId(id), item);
        return item;
    }

    public static void init() {
    }
}

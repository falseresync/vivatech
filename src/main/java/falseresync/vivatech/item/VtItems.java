package falseresync.vivatech.item;

import falseresync.vivatech.api.registry.RegistryObject;
import net.minecraft.item.Item;
import net.minecraft.util.Rarity;

public final class VtItems {
    public static final @RegistryObject Item RAW_ZINC = new Item(new Item.Settings().rarity(Rarity.UNCOMMON));
    public static final @RegistryObject Item ZINC_INGOT = new Item(new Item.Settings().rarity(Rarity.UNCOMMON));
    public static final @RegistryObject Item ZINC_NUGGET = new Item(new Item.Settings().rarity(Rarity.UNCOMMON));
    public static final @RegistryObject SacrificialDaggerItem SACRIFICIAL_DAGGER =
            new SacrificialDaggerItem(
                    VtToolMaterials.ZINC,
                    new Item.Settings().maxCount(1).maxDamage(256).rarity(Rarity.UNCOMMON)
                            .attributeModifiers(SacrificialDaggerItem.createAttributeModifiers(
                                    VtToolMaterials.ZINC, 2, -2.0f)));
    public static final @RegistryObject LifessenceAccumulatorItem LIFESSENCE_ACCUMULATOR =
            new LifessenceAccumulatorItem(100L, new Item.Settings().maxCount(1).rarity(Rarity.RARE));
}

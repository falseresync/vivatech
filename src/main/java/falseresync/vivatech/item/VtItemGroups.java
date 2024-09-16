package falseresync.vivatech.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;

import static falseresync.vivatech.Vivatech.vtId;

public final class VtItemGroups {
    public static final ItemGroup GENERAL = r("general", FabricItemGroup.builder()
            .icon(VtItems.SACRIFICIAL_DAGGER::getDefaultStack)
            .displayName(Text.translatable("itemGroup.vivatech.general"))
            .entries((displayContext, entries) -> {
                entries.add(VtItems.RAW_ZINC);
                entries.add(VtItems.ZINC_INGOT);
                entries.add(VtItems.ZINC_NUGGET);
                entries.add(VtItems.SACRIFICIAL_DAGGER);
            })
            .build());

    private static <T extends ItemGroup> T r(String id, T itemGroup) {
        Registry.register(Registries.ITEM_GROUP, vtId(id), itemGroup);
        return itemGroup;
    }

    public static void init() {}
}

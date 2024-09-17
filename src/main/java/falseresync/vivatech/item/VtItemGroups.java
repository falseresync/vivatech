package falseresync.vivatech.item;

import falseresync.vivatech.api.registry.RegistryObject;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;

public final class VtItemGroups {
    public static final @RegistryObject ItemGroup GENERAL = FabricItemGroup.builder()
            .icon(VtItems.SACRIFICIAL_DAGGER::getDefaultStack)
            .displayName(Text.translatable("itemGroup.vivatech.general"))
            .entries((displayContext, entries) -> {
                entries.add(VtItems.RAW_ZINC);
                entries.add(VtItems.ZINC_INGOT);
                entries.add(VtItems.ZINC_NUGGET);
                entries.add(VtItems.SACRIFICIAL_DAGGER);
                entries.add(VtItems.LIFE_ACCUMULATOR);
            })
            .build();
}

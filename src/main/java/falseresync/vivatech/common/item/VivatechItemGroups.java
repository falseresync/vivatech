package falseresync.vivatech.common.item;

import falseresync.vivatech.api.registry.RegistryObject;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.text.Text;

public final class VivatechItemGroups {
    public static final @RegistryObject ItemGroup GENERAL = FabricItemGroup.builder()
            .icon(VivatechItems.SACRIFICIAL_DAGGER::getDefaultStack)
            .displayName(Text.translatable("itemGroup.vivatech.general"))
            .entries((displayContext, entries) -> {
                entries.add(VivatechItems.RAW_ZINC);
                entries.add(VivatechItems.ZINC_INGOT);
                entries.add(VivatechItems.ZINC_NUGGET);
                entries.add(VivatechItems.SACRIFICIAL_DAGGER);
                entries.add(VivatechItems.LIFESSENCE_ACCUMULATOR);
            })
            .build();
}

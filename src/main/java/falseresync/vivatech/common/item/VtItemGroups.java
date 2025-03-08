package falseresync.vivatech.common.item;

import falseresync.lib.registry.RegistryObject;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class VtItemGroups {
    public static final @RegistryObject ItemGroup GENERAL =
            FabricItemGroup.builder()
                    .icon(Items.REDSTONE_BLOCK::getDefaultStack)
                    .displayName(Text.translatable("itemGroup.vivatech.general"))
                    .entries((displayContext, entries) -> {
                        entries.add(VtItems.GENERATOR);
                        entries.add(VtItems.GEARBOX);
                        entries.add(VtItems.WINDMILL);
                        entries.add(VtItems.HEATER);
                        entries.add(VtItems.WIRE_POST);

                        entries.add(VtItems.CONNECTOR);
                        entries.add(VtItems.PLIERS);
                    })
                    .build();
}

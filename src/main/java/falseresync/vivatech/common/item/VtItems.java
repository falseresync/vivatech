package falseresync.vivatech.common.item;

import falseresync.vivatech.common.block.VtBlocks;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.function.Function;

import static falseresync.vivatech.common.Vivatech.vtId;

public class VtItems {
    public static final BlockItem GENERATOR = rBlockItem("generator", VtBlocks.GENERATOR, new Item.Settings());
    public static final BlockItem WINDMILL = rBlockItem("windmill", VtBlocks.WINDMILL, new Item.Settings());
    public static final BlockItem HEATER = rBlockItem("heater", VtBlocks.HEATER, new Item.Settings());
    public static final BlockItem WIRE_POST = rBlockItem("wire_post", VtBlocks.WIRE_POST, new Item.Settings());

    public static final ConnectorItem CONNECTOR = r("connector", ConnectorItem::new, new Item.Settings().maxCount(1));
    public static final PliersItem PLIERS = r("pliers", PliersItem::new, new Item.Settings().maxCount(1));

    private static <T extends Item> T r(String id, Function<Item.Settings, T> item, Item.Settings settings) {
        var fullId = vtId(id);
        return Registry.register(Registries.ITEM, fullId, item.apply(settings));
    }

    private static <T extends Block> BlockItem rBlockItem(String id, T block, Item.Settings settings) {
        var fullId = vtId(id);
        return Registry.register(Registries.ITEM, fullId, new BlockItem(block, settings));
    }

    public static void registerAll() {
    }
}

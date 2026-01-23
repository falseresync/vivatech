package falseresync.vivatech.world.item;

import falseresync.vivatech.Vivatech;
import falseresync.vivatech.world.block.VivatechBlocks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

public class VivatechItems {
    public static final BlockItem GENERATOR
            = register("generator", VivatechBlocks.GENERATOR);
    public static final BlockItem GEARBOX
            = register("gearbox", VivatechBlocks.GEARBOX);
    public static final BlockItem WIND_TURBINE
            = register("wind_turbine", VivatechBlocks.WIND_TURBINE);

    public static final BlockItem HEATER
            = register("heater", VivatechBlocks.HEATER);

    public static final BlockItem STATIC_COMPENSATOR
            = register("static_compensator", VivatechBlocks.STATIC_COMPENSATOR);
    public static final BlockItem CONTACTOR
            = register("contactor", VivatechBlocks.CONTACTOR);

    public static final BlockItem WIRE_POST
            = register("wire_post", VivatechBlocks.WIRE_POST);

    public static final Item WIRE
            = register("wire", WireItem::new);
    public static final Item MORTAR_AND_PESTLE
            = register("mortar_and_pestle", MortarAndPestleItem::new, new Item.Properties().stacksTo(1).durability(16));
    public static final Item SCREWDRIVER
            = register("screwdriver", Item::new, new Item.Properties().stacksTo(1));
    public static final Item PLIERS
            = register("pliers", PliersItem::new, new Item.Properties().stacksTo(1));
    public static final Item PROBE
            = register("probe", ProbeItem::new, new Item.Properties().stacksTo(1));

    public static final Item VIVATECH_BOOK
            = register("vivatech_book", VivatechBookItem::new);

    public static void init() {}

    private static Item register(String name, Function<Item.Properties, Item> itemFactory) {
        return register(name, itemFactory, new Item.Properties());
    }

    private static Item register(String name, Function<Item.Properties, Item> itemFactory, Item.Properties properties) {
        var id = Vivatech.id(name);
        return Registry.register(BuiltInRegistries.ITEM, id, itemFactory.apply(properties.setId(ResourceKey.create(Registries.ITEM, id))));
    }

    private static BlockItem register(String name, Block block) {
        return register(name, block, new Item.Properties());
    }

    private static BlockItem register(String name, Block block, Item.Properties properties) {
        var id = Vivatech.id(name);
        return Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(block, properties
                .setId(ResourceKey.create(Registries.ITEM, id))
                .useBlockDescriptionPrefix()));
    }
}

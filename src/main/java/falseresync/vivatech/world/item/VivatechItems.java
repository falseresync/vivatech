package falseresync.vivatech.world.item;

import falseresync.vivatech.Vivatech;
import falseresync.vivatech.world.block.VivatechBlocks;
import falseresync.vivatech.world.electricity.wire.WireType;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.function.Function;

public class VivatechItems {
    public static final BlockItem GENERATOR = register(VivatechBlocks.GENERATOR);
    public static final BlockItem GEARBOX = register(VivatechBlocks.GEARBOX);
    public static final BlockItem WIND_TURBINE = register(VivatechBlocks.WIND_TURBINE);

    public static final BlockItem HEATER = register(VivatechBlocks.HEATER);

    public static final BlockItem STATIC_COMPENSATOR = register(VivatechBlocks.STATIC_COMPENSATOR);
    public static final BlockItem CONTACTOR = register(VivatechBlocks.CONTACTOR);

    public static final BlockItem WIRE_POST = register(VivatechBlocks.WIRE_POST);

    public static final BlockItem MACHINE_CHASSIS = register(VivatechBlocks.MACHINE_CHASSIS);

    public static final BlockItem CUPROSTEEL_BLOCK = register(VivatechBlocks.CUPROSTEEL_BLOCK);
    public static final Item CUPROSTEEL_INGOT
            = register("cuprosteel_ingot", Item::new);
    public static final Item CUPROSTEEL_NUGGET
            = register("cuprosteel_nugget", Item::new);

    public static final BlockItem BRASS_BLOCK = register( VivatechBlocks.BRASS_BLOCK);
    public static final Item BRASS_INGOT
            = register("brass_ingot", Item::new);
    public static final Item BRASS_NUGGET
            = register("brass_nugget", Item::new);

    public static final Item COPPER_WIRE
            = register("copper_wire", properties -> new WireItem(properties, WireType.V_230));
    public static final Item CUPROSTEEL_WIRE
            = register("cuprosteel_wire", properties -> new WireItem(properties, WireType.V_20K));
    public static final Item THICK_CURPOSTEEL_WIRE
            = register("thick_cuprosteel_wire", properties -> new WireItem(properties, WireType.V_330K));
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

    private static BlockItem register(Block block) {
        return register(block.properties().getIdOrThrow().identifier().getPath(), block, new Item.Properties());
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

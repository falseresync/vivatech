package falseresync.vivatech.common.item;

import falseresync.vivatech.common.block.VivatechBlocks;
import falseresync.vivatech.common.item.FocusesPouchItem;
import falseresync.vivatech.common.item.GadgetItem;
import falseresync.vivatech.common.item.InspectorGogglesItem;
import falseresync.vivatech.common.item.MortarAndPestleItem;
import falseresync.vivatech.common.item.PliersItem;
import falseresync.vivatech.common.item.ProbeItem;
import falseresync.vivatech.common.item.WireItem;
import falseresync.vivatech.common.item.focus.CometWarpFocusItem;
import falseresync.vivatech.common.item.focus.EnergyVeilFocusItem;
import falseresync.vivatech.common.item.focus.LightningFocusItem;
import falseresync.vivatech.common.item.focus.StarshooterFocusItem;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;

import static falseresync.vivatech.common.Vivatech.vtId;

public class VivatechItems {
    public static final BlockItem GENERATOR = rBlockItem("generator", VivatechBlocks.GENERATOR, new Item.Properties());
    public static final BlockItem GEARBOX = rBlockItem("gearbox", VivatechBlocks.GEARBOX, new Item.Properties());
    public static final BlockItem WIND_TURBINE = rBlockItem("wind_turbine", VivatechBlocks.WIND_TURBINE, new Item.Properties());

    public static final BlockItem HEATER = rBlockItem("heater", VivatechBlocks.HEATER, new Item.Properties());
    public static final BlockItem CHARGER = rBlockItem("charger", VivatechBlocks.CHARGER, new Item.Properties());

    public static final BlockItem STATIC_COMPENSATOR = rBlockItem("static_compensator", VivatechBlocks.STATIC_COMPENSATOR, new Item.Properties());
    public static final BlockItem CONTACTOR = rBlockItem("contactor", VivatechBlocks.CONTACTOR, new Item.Properties());

    public static final BlockItem WIRE_POST = rBlockItem("wire_post", VivatechBlocks.WIRE_POST, new Item.Properties());

    public static final falseresync.vivatech.common.item.WireItem WIRE = r("wire", WireItem::new, new Item.Properties());
    public static final falseresync.vivatech.common.item.MortarAndPestleItem MORTAR_AND_PESTLE = r("mortar_and_pestle", MortarAndPestleItem::new, new Item.Properties().stacksTo(1).durability(16));
    public static final Item SCREWDRIVER = r("screwdriver", Item::new, new Item.Properties().stacksTo(1));
    public static final falseresync.vivatech.common.item.PliersItem PLIERS = r("pliers", PliersItem::new, new Item.Properties().stacksTo(1));
    public static final falseresync.vivatech.common.item.ProbeItem PROBE = r("probe", ProbeItem::new, new Item.Properties().stacksTo(1));
    public static final falseresync.vivatech.common.item.InspectorGogglesItem INSPECTOR_GOGGLES = r("inspector_goggles", InspectorGogglesItem::new, new Item.Properties().stacksTo(1));
    public static final falseresync.vivatech.common.item.FocusesPouchItem FOCUSES_POUCH = r("focuses_pouch", FocusesPouchItem::new, new Item.Properties().stacksTo(1));

    public static final falseresync.vivatech.common.item.GadgetItem GADGET = r("gadget", GadgetItem::new, new Item.Properties().stacksTo(1));

    public static final Item.Properties FOCUS_SETTINGS = new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON);
    public static final StarshooterFocusItem STARSHOOTER_FOCUS = r("starshooter_focus", StarshooterFocusItem::new, FOCUS_SETTINGS.durability(512));
    public static final LightningFocusItem LIGHTNING_FOCUS = r("lightning_focus", LightningFocusItem::new, FOCUS_SETTINGS.durability(128));
    public static final CometWarpFocusItem COMET_WARP_FOCUS = r("comet_warp_focus", CometWarpFocusItem::new, FOCUS_SETTINGS.durability(16));
    public static final EnergyVeilFocusItem ENERGY_VEIL_FOCUS = r("energy_veil_focus", EnergyVeilFocusItem::new, FOCUS_SETTINGS.durability(64));

    private static <T extends Item> T r(String id, Function<Item.Properties, T> item, Item.Properties settings) {
        var fullId = vtId(id);
        return Registry.register(BuiltInRegistries.ITEM, fullId, item.apply(settings));
    }

    private static <T extends Block> BlockItem rBlockItem(String id, T block, Item.Properties settings) {
        var fullId = vtId(id);
        return Registry.register(BuiltInRegistries.ITEM, fullId, new BlockItem(block, settings));
    }

    public static void registerAll() {
    }
}

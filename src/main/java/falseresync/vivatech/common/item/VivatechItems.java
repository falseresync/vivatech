package falseresync.vivatech.common.item;

import falseresync.vivatech.common.block.VivatechBlocks;
import falseresync.vivatech.common.item.focus.CometWarpFocusItem;
import falseresync.vivatech.common.item.focus.EnergyVeilFocusItem;
import falseresync.vivatech.common.item.focus.LightningFocusItem;
import falseresync.vivatech.common.item.focus.StarshooterFocusItem;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Rarity;

import java.util.function.Function;

import static falseresync.vivatech.common.Vivatech.vtId;

public class VivatechItems {
    public static final BlockItem GENERATOR = rBlockItem("generator", VivatechBlocks.GENERATOR, new Item.Settings());
    public static final BlockItem GEARBOX = rBlockItem("gearbox", VivatechBlocks.GEARBOX, new Item.Settings());
    public static final BlockItem WINDMILL = rBlockItem("windmill", VivatechBlocks.WINDMILL, new Item.Settings());

    public static final BlockItem HEATER = rBlockItem("heater", VivatechBlocks.HEATER, new Item.Settings());
    public static final BlockItem STATIC_COMPENSATOR = rBlockItem("static_compensator", VivatechBlocks.STATIC_COMPENSATOR, new Item.Settings());
    public static final BlockItem CHARGER = rBlockItem("charger", VivatechBlocks.CHARGER, new Item.Settings());

    public static final BlockItem WIRE_POST = rBlockItem("wire_post", VivatechBlocks.WIRE_POST, new Item.Settings());

    public static final MortarAndPestleItem MORTAR_AND_PESTLE = r("mortar_and_pestle", MortarAndPestleItem::new, new Item.Settings().maxCount(1).maxDamage(16));
    public static final WireItem WIRE = r("wire", WireItem::new, new Item.Settings());
    public static final PliersItem PLIERS = r("pliers", PliersItem::new, new Item.Settings().maxCount(1));

    public static final GadgetItem GADGET = r("gadget", GadgetItem::new, new Item.Settings().maxCount(1));

    public static final Item.Settings FOCUS_SETTINGS = new Item.Settings().maxCount(1).rarity(Rarity.UNCOMMON);
    public static final StarshooterFocusItem STARSHOOTER_FOCUS = r("starshooter_focus", StarshooterFocusItem::new, FOCUS_SETTINGS.maxDamage(512));
    public static final LightningFocusItem LIGHTNING_FOCUS = r("lightning_focus", LightningFocusItem::new, FOCUS_SETTINGS.maxDamage(128));
    public static final CometWarpFocusItem COMET_WARP_FOCUS = r("comet_warp_focus", CometWarpFocusItem::new, FOCUS_SETTINGS.maxDamage(16));
    public static final EnergyVeilFocusItem ENERGY_VEIL_FOCUS = r("energy_veil_focus", EnergyVeilFocusItem::new, FOCUS_SETTINGS.maxDamage(64));

    public static final InspectorGogglesItem INSPECTOR_GOGGLES = r("inspector_goggles", InspectorGogglesItem::new, new Item.Settings().maxCount(1));
    public static final FocusesPouchItem FOCUSES_POUCH = r("focuses_pouch", FocusesPouchItem::new, new Item.Settings().maxCount(1));

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

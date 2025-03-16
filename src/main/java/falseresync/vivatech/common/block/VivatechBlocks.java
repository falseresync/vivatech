package falseresync.vivatech.common.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.function.Function;

import static falseresync.vivatech.common.Vivatech.vtId;

public class VivatechBlocks {
    public static final GeneratorBlock GENERATOR = r("generator", GeneratorBlock::new, AbstractBlock.Settings.copy(Blocks.CAULDRON));
    public static final GearboxBlock GEARBOX = r("gearbox", GearboxBlock::new, AbstractBlock.Settings.copy(Blocks.CAULDRON));
    public static final WindTurbineBlock WIND_TURBINE = r("wind_turbine", WindTurbineBlock::new, AbstractBlock.Settings.copy(Blocks.CAULDRON));

    public static final HeaterBlock HEATER = r("heater", HeaterBlock::new, AbstractBlock.Settings.copy(Blocks.COPPER_BLOCK));
    public static final ChargerBlock CHARGER = r("charger", ChargerBlock::new, AbstractBlock.Settings.copy(Blocks.COPPER_BLOCK));

    public static final StaticCompensatorBlock STATIC_COMPENSATOR = r("static_compensator", StaticCompensatorBlock::new, AbstractBlock.Settings.copy(Blocks.COPPER_BLOCK));
    public static final ContactorBlock CONTACTOR = r("contactor", ContactorBlock::new, AbstractBlock.Settings.copy(Blocks.COPPER_BLOCK).pistonBehavior(PistonBehavior.BLOCK));

    public static final WirePostBlock WIRE_POST = r("wire_post", WirePostBlock::new, AbstractBlock.Settings.copy(Blocks.LIGHTNING_ROD));

    private static <T extends Block> T r(String id, Function<AbstractBlock.Settings, T> block, AbstractBlock.Settings settings) {
        var fullId = vtId(id);
        return Registry.register(Registries.BLOCK, fullId, block.apply(settings));
    }

    public static void registerAll() {
    }
}

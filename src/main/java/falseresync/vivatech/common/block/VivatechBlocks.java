package falseresync.vivatech.common.block;

import java.util.function.Function;

import falseresync.vivatech.common.block.ChargerBlock;
import falseresync.vivatech.common.block.ContactorBlock;
import falseresync.vivatech.common.block.GearboxBlock;
import falseresync.vivatech.common.block.GeneratorBlock;
import falseresync.vivatech.common.block.HeaterBlock;
import falseresync.vivatech.common.block.StaticCompensatorBlock;
import falseresync.vivatech.common.block.WindTurbineBlock;
import falseresync.vivatech.common.block.WirePostBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

import static falseresync.vivatech.common.Vivatech.vtId;

public class VivatechBlocks {
    public static final falseresync.vivatech.common.block.GeneratorBlock GENERATOR = r("generator", GeneratorBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON));
    public static final falseresync.vivatech.common.block.GearboxBlock GEARBOX = r("gearbox", GearboxBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON));
    public static final falseresync.vivatech.common.block.WindTurbineBlock WIND_TURBINE = r("wind_turbine", WindTurbineBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON));

    public static final falseresync.vivatech.common.block.HeaterBlock HEATER = r("heater", HeaterBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK));
    public static final falseresync.vivatech.common.block.ChargerBlock CHARGER = r("charger", ChargerBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK));

    public static final falseresync.vivatech.common.block.StaticCompensatorBlock STATIC_COMPENSATOR = r("static_compensator", StaticCompensatorBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK));
    public static final falseresync.vivatech.common.block.ContactorBlock CONTACTOR = r("contactor", ContactorBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).pushReaction(PushReaction.BLOCK));

    public static final falseresync.vivatech.common.block.WirePostBlock WIRE_POST = r("wire_post", WirePostBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.LIGHTNING_ROD));

    private static <T extends Block> T r(String id, Function<BlockBehaviour.Properties, T> block, BlockBehaviour.Properties settings) {
        var fullId = vtId(id);
        return Registry.register(BuiltInRegistries.BLOCK, fullId, block.apply(settings));
    }

    public static void registerAll() {
    }
}

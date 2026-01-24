package falseresync.vivatech.world.block;

import falseresync.vivatech.Vivatech;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Function;

public class VivatechBlocks {
    public static final GeneratorBlock GENERATOR
            = register("generator", GeneratorBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON));
    public static final GearboxBlock GEARBOX
            = register("gearbox", GearboxBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON));
    public static final WindTurbineBlock WIND_TURBINE
            = register("wind_turbine", WindTurbineBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.CAULDRON));

    public static final HeaterBlock HEATER
            = register("heater", HeaterBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK));

    public static final StaticCompensatorBlock STATIC_COMPENSATOR
            = register("static_compensator", StaticCompensatorBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).pushReaction(PushReaction.BLOCK));
    public static final ContactorBlock CONTACTOR
            = register("contactor", ContactorBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.COPPER_BLOCK).pushReaction(PushReaction.BLOCK));

    public static final WirePostBlock WIRE_POST
            = register("wire_post", WirePostBlock::new, BlockBehaviour.Properties.ofFullCopy(Blocks.LIGHTNING_ROD));

    public static final Block MACHINE_CHASSIS
            = register("machine_chassis", Block::new, BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));

    public static final Block CUPROSTEEL_BLOCK
            = register("cuprosteel_block", Block::new, BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));
    public static final Block BRASS_BLOCK
            = register("brass_block", Block::new, BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK));

    private static <T extends Block> T register(String name, Function<BlockBehaviour.Properties, T> block, BlockBehaviour.Properties settings) {
        var id = Vivatech.id(name);
        return Registry.register(BuiltInRegistries.BLOCK, id, block.apply(settings.setId(ResourceKey.create(Registries.BLOCK, id))));
    }

    public static void init() {
    }
}

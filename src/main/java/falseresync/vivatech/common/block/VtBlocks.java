package falseresync.vivatech.common.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.function.Function;

import static falseresync.vivatech.common.Vivatech.vtId;

public class VtBlocks {
    public static final GeneratorBlock GENERATOR = r("generator", GeneratorBlock::new, AbstractBlock.Settings.copy(Blocks.CAULDRON));
    public static final ConsumerBlock CONSUMER = r("consumer", ConsumerBlock::new, AbstractBlock.Settings.copy(Blocks.CAULDRON));
    public static final WirePostBlock WIRE_POST = r("wire_post", WirePostBlock::new, AbstractBlock.Settings.copy(Blocks.LIGHTNING_ROD));

    private static <T extends Block> T r(String id, Function<AbstractBlock.Settings, T> block, AbstractBlock.Settings settings) {
        var fullId = vtId(id);
        return Registry.register(Registries.BLOCK, fullId, block.apply(settings));
    }

    public static void registerAll() {
    }
}

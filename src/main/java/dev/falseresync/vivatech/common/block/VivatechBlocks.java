package dev.falseresync.vivatech.common.block;

import dev.falseresync.vivatech.common.block.sterling_generator.SterlingGeneratorBlockEntity;
import dev.falseresync.vivatech.api.HasId;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.function.Supplier;

import static dev.falseresync.vivatech.common.VivatechConsts.vivatech;

public class VivatechBlocks {
    private static final HashMap<Identifier, Block> TO_REGISTER = new HashMap<>();
    private static final FabricBlockSettings MACHINE_SETTINGS = FabricBlockSettings.copyOf(Blocks.CAULDRON);
    public static final MachineBlock<SterlingGeneratorBlockEntity> STERLING_GENERATOR
            = rSimpleMachine(vivatech("sterling_generator"), () -> VivatechBlockEntities.STERLING_GENERATOR, SterlingGeneratorBlockEntity::tick, MACHINE_SETTINGS);

    private static <T extends Block & HasId> T r(T block) {
        TO_REGISTER.put(block.getId(), block);
        return block;
    }

    private static <BE extends MachineBlockEntity> MachineBlock<BE> rSimpleMachine(Identifier id, Supplier<BlockEntityType<BE>> type, BlockEntityTicker<BE> ticker, AbstractBlock.Settings settings) {
        return r(new MachineBlock<>(id, type, ticker, settings) {});
    }

    public static void register() {
        TO_REGISTER.forEach((identifier, type) -> Registry.register(Registries.BLOCK, identifier, type));
    }
}

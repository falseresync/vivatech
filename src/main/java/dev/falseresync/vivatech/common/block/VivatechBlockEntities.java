package dev.falseresync.vivatech.common.block;

import dev.falseresync.vivatech.common.block.sterling_generator.SterlingGeneratorBlockEntity;
import dev.falseresync.vivatech.api.HasId;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.HashMap;

public class VivatechBlockEntities {
    private static final HashMap<Identifier, BlockEntityType<?>> TO_REGISTER = new HashMap<>();
    public static final BlockEntityType<SterlingGeneratorBlockEntity> STERLING_GENERATOR =
            r(VivatechBlocks.STERLING_GENERATOR.getId(), SterlingGeneratorBlockEntity::new, VivatechBlocks.STERLING_GENERATOR);

    private static <T extends BlockEntity, B extends Block & HasId> BlockEntityType<T> r(Identifier id, FabricBlockEntityTypeBuilder.Factory<T> factory, B... blocks) {
        var type = FabricBlockEntityTypeBuilder.create(factory).addBlocks(blocks).build();
        TO_REGISTER.put(id, type);
        return type;
    }

    public static void register() {
        TO_REGISTER.forEach((identifier, type) -> Registry.register(Registries.BLOCK_ENTITY_TYPE, identifier, type));
    }
}

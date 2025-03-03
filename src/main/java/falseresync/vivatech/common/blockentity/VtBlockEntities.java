package falseresync.vivatech.common.blockentity;

import falseresync.vivatech.common.block.VtBlocks;
import falseresync.lib.registry.RegistryObject;
import net.minecraft.block.entity.BlockEntityType;

public class VtBlockEntities {
    public static final @RegistryObject BlockEntityType<GeneratorBlockEntity> GENERATOR =
            BlockEntityType.Builder.create(GeneratorBlockEntity::new, VtBlocks.GENERATOR).build();
    public static final @RegistryObject BlockEntityType<ConsumerBlockEntity> CONSUMER =
            BlockEntityType.Builder.create(ConsumerBlockEntity::new, VtBlocks.CONSUMER).build();
    public static final @RegistryObject BlockEntityType<WirePostBlockEntity> WIRE_POST =
            BlockEntityType.Builder.create(WirePostBlockEntity::new, VtBlocks.WIRE_POST).build();
}

package falseresync.vivatech.common.blockentity;

import falseresync.vivatech.common.block.VtBlocks;
import falseresync.lib.registry.RegistryObject;
import net.minecraft.block.entity.BlockEntityType;

public class VtBlockEntities {
    public static final @RegistryObject BlockEntityType<GeneratorBlockEntity> GENERATOR =
            BlockEntityType.Builder.create(GeneratorBlockEntity::new, VtBlocks.GENERATOR).build();
    public static final @RegistryObject BlockEntityType<WindmillBlockEntity> WINDMILL =
            BlockEntityType.Builder.create(WindmillBlockEntity::new, VtBlocks.WINDMILL).build();
    public static final @RegistryObject BlockEntityType<HeaterBlockEntity> HEATER =
            BlockEntityType.Builder.create(HeaterBlockEntity::new, VtBlocks.HEATER).build();
}

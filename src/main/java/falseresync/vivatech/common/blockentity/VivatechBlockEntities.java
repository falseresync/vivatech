package falseresync.vivatech.common.blockentity;

import falseresync.vivatech.common.block.VivatechBlocks;
import falseresync.lib.registry.RegistryObject;
import net.minecraft.block.entity.BlockEntityType;

public class VivatechBlockEntities {
    public static final @RegistryObject BlockEntityType<GeneratorBlockEntity> GENERATOR =
            BlockEntityType.Builder.create(GeneratorBlockEntity::new, VivatechBlocks.GENERATOR).build();
    public static final @RegistryObject BlockEntityType<WindTurbineBlockEntity> WIND_TURBINE =
            BlockEntityType.Builder.create(WindTurbineBlockEntity::new, VivatechBlocks.WIND_TURBINE).build();

    public static final @RegistryObject BlockEntityType<HeaterBlockEntity> HEATER =
            BlockEntityType.Builder.create(HeaterBlockEntity::new, VivatechBlocks.HEATER).build();
    public static final @RegistryObject BlockEntityType<StaticCompensatorBlockEntity> STATIC_COMPENSATOR =
            BlockEntityType.Builder.create(StaticCompensatorBlockEntity::new, VivatechBlocks.STATIC_COMPENSATOR).build();
    public static final @RegistryObject BlockEntityType<ChargerBlockEntity> CHARGER =
            BlockEntityType.Builder.create(ChargerBlockEntity::new, VivatechBlocks.CHARGER).build();
}

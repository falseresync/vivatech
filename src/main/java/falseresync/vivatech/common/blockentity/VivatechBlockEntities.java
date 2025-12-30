package falseresync.vivatech.common.blockentity;

import falseresync.vivatech.common.block.VivatechBlocks;
import falseresync.vivatech.common.blockentity.ChargerBlockEntity;
import falseresync.vivatech.common.blockentity.GeneratorBlockEntity;
import falseresync.vivatech.common.blockentity.HeaterBlockEntity;
import falseresync.vivatech.common.blockentity.StaticCompensatorBlockEntity;
import falseresync.vivatech.common.blockentity.WindTurbineBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import falseresync.lib.registry.RegistryObject;

public class VivatechBlockEntities {
    public static final @RegistryObject BlockEntityType<falseresync.vivatech.common.blockentity.GeneratorBlockEntity> GENERATOR =
            BlockEntityType.Builder.of(GeneratorBlockEntity::new, VivatechBlocks.GENERATOR).build();
    public static final @RegistryObject BlockEntityType<falseresync.vivatech.common.blockentity.WindTurbineBlockEntity> WIND_TURBINE =
            BlockEntityType.Builder.of(WindTurbineBlockEntity::new, VivatechBlocks.WIND_TURBINE).build();

    public static final @RegistryObject BlockEntityType<falseresync.vivatech.common.blockentity.HeaterBlockEntity> HEATER =
            BlockEntityType.Builder.of(HeaterBlockEntity::new, VivatechBlocks.HEATER).build();
    public static final @RegistryObject BlockEntityType<falseresync.vivatech.common.blockentity.ChargerBlockEntity> CHARGER =
            BlockEntityType.Builder.of(ChargerBlockEntity::new, VivatechBlocks.CHARGER).build();

    public static final @RegistryObject BlockEntityType<falseresync.vivatech.common.blockentity.StaticCompensatorBlockEntity> STATIC_COMPENSATOR =
            BlockEntityType.Builder.of(StaticCompensatorBlockEntity::new, VivatechBlocks.STATIC_COMPENSATOR).build();
}

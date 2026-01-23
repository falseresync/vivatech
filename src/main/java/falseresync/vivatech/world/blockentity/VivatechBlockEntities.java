package falseresync.vivatech.world.blockentity;

import falseresync.vivatech.Vivatech;
import falseresync.vivatech.world.block.VivatechBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class VivatechBlockEntities {
    public static final BlockEntityType<GeneratorBlockEntity> GENERATOR =
            register("generator", GeneratorBlockEntity::new, VivatechBlocks.GENERATOR);
    public static final BlockEntityType<WindTurbineBlockEntity> WIND_TURBINE =
            register("wind_turbine", WindTurbineBlockEntity::new, VivatechBlocks.WIND_TURBINE);

    public static final BlockEntityType<HeaterBlockEntity> HEATER =
            register("heater", HeaterBlockEntity::new, VivatechBlocks.HEATER);

    public static final BlockEntityType<StaticCompensatorBlockEntity> STATIC_COMPENSATOR =
            register("static_compensator", StaticCompensatorBlockEntity::new, VivatechBlocks.STATIC_COMPENSATOR);

    private static <T extends BlockEntity> BlockEntityType<T> register(String name, FabricBlockEntityTypeBuilder.Factory<T> factory, Block... blocks) {
        return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Vivatech.id(name), FabricBlockEntityTypeBuilder.create(factory, blocks).build());
    }

    public static void init() {}
}

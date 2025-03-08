package falseresync.vivatech.common.entity;

import falseresync.lib.registry.RegistryObject;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;

public class VivatechEntities {
    public static final @RegistryObject EntityType<StarProjectileEntity> STAR_PROJECTILE = EntityType.Builder
            .<StarProjectileEntity>create(StarProjectileEntity::new, SpawnGroup.MISC)
            .dimensions(0.5F, 0.5F)
            .makeFireImmune()
            .disableSaving()
            .maxTrackingRange(16)
            .build();
    public static final @RegistryObject EntityType<EnergyVeilEntity> ENERGY_VEIL = EntityType.Builder
            .<EnergyVeilEntity>create(EnergyVeilEntity::new, SpawnGroup.MISC)
            .dimensions(0F, 0F)
            .makeFireImmune()
            .disableSaving()
            .maxTrackingRange(16)
            .build();
}

package falseresync.vivatech.common.entity;

import falseresync.lib.registry.RegistryObject;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class VivatechEntities {
    public static final @RegistryObject EntityType<StarProjectileEntity> STAR_PROJECTILE = EntityType.Builder
            .<StarProjectileEntity>of(StarProjectileEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.5F)
            .fireImmune()
            .noSave()
            .clientTrackingRange(16)
            .build();
    public static final @RegistryObject EntityType<EnergyVeilEntity> ENERGY_VEIL = EntityType.Builder
            .<falseresync.vivatech.common.entity.EnergyVeilEntity>of(EnergyVeilEntity::new, MobCategory.MISC)
            .sized(0F, 0F)
            .fireImmune()
            .noSave()
            .clientTrackingRange(16)
            .build();
}

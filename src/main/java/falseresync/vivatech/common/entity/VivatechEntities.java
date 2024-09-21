package falseresync.vivatech.common.entity;

import falseresync.vivatech.api.registry.RegistryObject;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;

public class VivatechEntities {
    public static final @RegistryObject EntityType<AutomatonEntity> AUTOMATON = FabricEntityType.Builder
            .createMob(AutomatonEntity::new, SpawnGroup.MISC, automaton -> automaton
                    .defaultAttributes(AutomatonEntity::createAutomatonAttributes))
            .dimensions(16 / 16f, 16 / 16f)
            .eyeHeight(14 / 16f)
            .build("automaton");
}

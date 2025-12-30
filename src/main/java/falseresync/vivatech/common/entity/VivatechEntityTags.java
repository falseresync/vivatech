package falseresync.vivatech.common.entity;

import static falseresync.vivatech.common.Vivatech.vtId;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

public class VivatechEntityTags {
    public static final TagKey<EntityType<?>> PASSES_THROUGH_ENERGY_VEIL = TagKey.create(Registries.ENTITY_TYPE, vtId("passes_through_energy_veil"));

    public static final TagKey<EntityType<?>> TRANSMUTATION_AGEABLE = TagKey.create(Registries.ENTITY_TYPE, vtId("transmutation/ageable"));
    public static final TagKey<EntityType<?>> TRANSMUTATION_TRANSFORMABLE = TagKey.create(Registries.ENTITY_TYPE, vtId("transmutation/transformable"));
    public static final TagKey<EntityType<?>> TRANSMUTATION_RESULT = TagKey.create(Registries.ENTITY_TYPE, vtId("transmutation/results"));
}

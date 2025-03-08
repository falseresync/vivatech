package falseresync.vivatech.common.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

import static falseresync.vivatech.common.Vivatech.vtId;

public class VivatechEntityTags {
    public static final TagKey<EntityType<?>> PASSES_THROUGH_ENERGY_VEIL = TagKey.of(RegistryKeys.ENTITY_TYPE, vtId("passes_through_energy_veil"));

    public static final TagKey<EntityType<?>> TRANSMUTATION_AGEABLE = TagKey.of(RegistryKeys.ENTITY_TYPE, vtId("transmutation/ageable"));
    public static final TagKey<EntityType<?>> TRANSMUTATION_TRANSFORMABLE = TagKey.of(RegistryKeys.ENTITY_TYPE, vtId("transmutation/transformable"));
    public static final TagKey<EntityType<?>> TRANSMUTATION_RESULT = TagKey.of(RegistryKeys.ENTITY_TYPE, vtId("transmutation/results"));
}

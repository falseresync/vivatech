package falseresync.lib.registry;

import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;

import java.lang.reflect.InaccessibleObjectException;
import java.lang.reflect.Modifier;

/**
 * <h2>USAGE EXAMPLE</h2>
 *
 * <p>In your ModInitializer:
 * <pre>{@code
 * new AutoRegistry("mod_id", LOGGER)
 *      .link(Registries.BLOCK, MyBlocks.class)
 *      .link(Registries.ITEM, MyItems.class);
 * }</pre>
 *
 * <p>In your MyBlocks/Items/etc classes:
 * <pre>{@code
 * public class MyBlocks {
 *     public static final @RegistryObject MagicCauldronBlock MAGIC_CAULDRON = new MagicCauldronBlock();
 * }
 * }</pre>
 */
public class AutoRegistry {
    private final String modId;
    private final Logger logger;

    public AutoRegistry(String modId, Logger logger) {
        this.modId = modId;
        this.logger = logger;
    }

    /**
     * Registers all the objects in the provided class into the provided registry by the following rules:
     * <ul>
     *     <li>Only the public static fields marked with @RegistryObject are scanned</li>
     *     <li>The ID is formed by lower-casing the field name</li>
     *     <li>Null fields are discarded with a warning message</li>
     * </ul>
     * @param <T> type parameter must match the type of the fields
     * @implNote help
     */
    public <T> AutoRegistry link(Registry<T> registry, Class<?>... holderClasses) {
        for (var holderClass : holderClasses) {
            for (var field : holderClass.getDeclaredFields()) {
                var modifiers = field.getModifiers();
                var annotations = field.getAnnotationsByType(RegistryObject.class);
                if (!Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers) || annotations.length == 0) {
                    continue;
                }

                try {
                    var registryObject = field.get(holderClass);
                    if (registryObject == null) {
                        logger.warn("Found a null @RegistryObject field, discarding: %s at %s".formatted(field.getName(), holderClass.getCanonicalName()));
                        continue;
                    }

                    //noinspection unchecked
                    Registry.register(registry, Identifier.of(modId, field.getName().toLowerCase()), (T) registryObject);
                } catch (IllegalAccessException e) {
                    throw new InaccessibleObjectException("Couldn't read a @RegistryObject field: %s at %s".formatted(field.getName(), holderClass.getCanonicalName()));
                } catch (ClassCastException e) {
                    throw new IllegalArgumentException("A @RegistryObject field's type doesn't match the provided registry: %s at %s".formatted(field.getName(), holderClass.getCanonicalName()));
                }
            }
        }
        return this;
    }
}
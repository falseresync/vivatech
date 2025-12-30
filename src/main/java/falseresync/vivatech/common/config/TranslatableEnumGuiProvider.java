package falseresync.vivatech.common.config;

import me.shedaniel.autoconfig.gui.registry.api.GuiProvider;
import me.shedaniel.autoconfig.gui.registry.api.GuiRegistryAccess;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.function.BinaryOperator;

import static me.shedaniel.autoconfig.util.Utils.getUnsafely;
import static me.shedaniel.autoconfig.util.Utils.setUnsafely;

public class TranslatableEnumGuiProvider<T extends Enum<?>> implements GuiProvider {
    private static final BinaryOperator<String> NAME_PROVIDER = (optionName, enumName) -> optionName + "." + enumName;

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public List<AbstractConfigListEntry> get(String i18n, Field field, Object config, Object defaults, GuiRegistryAccess guiProvider) {
        return Collections.singletonList(
                ConfigEntryBuilder.create().startEnumSelector(
                                Component.translatable(i18n),
                                (Class<T>) field.getType(),
                                getUnsafely(field, config, getUnsafely(field, defaults))
                        )
                        .setDefaultValue(() -> getUnsafely(field, defaults))
                        .setSaveConsumer(newValue -> setUnsafely(field, config, newValue))
                        .setEnumNameProvider(anEnum -> Component.translatable(NAME_PROVIDER.apply(i18n, anEnum.name())))
                        .build()
        );
    }
}

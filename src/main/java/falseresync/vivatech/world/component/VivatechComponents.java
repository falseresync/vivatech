package falseresync.vivatech.world.component;

import falseresync.vivatech.Vivatech;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;


public class VivatechComponents {
    public static final DataComponentType<GlobalPos> CONNECTION =
            register("connection", DataComponentType.<GlobalPos>builder()
                    .persistent(GlobalPos.CODEC)
                    .networkSynchronized(GlobalPos.STREAM_CODEC));
    public static final DataComponentType<ItemBarComponent> ITEM_BAR =
            register("item_bar", DataComponentType.<ItemBarComponent>builder()
                    .persistent(ItemBarComponent.CODEC)
                    .networkSynchronized(ItemBarComponent.PACKET_CODEC));

    private static <T> DataComponentType<T> register(String name, DataComponentType.Builder<T> builder) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Vivatech.id(name), builder.build());
    }

    public static void init() {}
}

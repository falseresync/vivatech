package falseresync.vivatech.common.component.item;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import falseresync.vivatech.api.registry.RegistryObject;
import net.minecraft.component.ComponentType;
import net.minecraft.network.codec.PacketCodecs;

public final class VivatechItemComponents {
    public static final @RegistryObject ComponentType<Long> LIFESSENCE = ComponentType.<Long>builder()
            .codec(nonNegativeLong()).packetCodec(PacketCodecs.VAR_LONG).build();

    private static Codec<Long> nonNegativeLong() {
        return Codec.LONG.validate((Long value) -> {
            if (value >= 0) {
                return DataResult.success(value);
            }

            return DataResult.error(() -> "Value must be non-negative: " + value);
        });
    }
}

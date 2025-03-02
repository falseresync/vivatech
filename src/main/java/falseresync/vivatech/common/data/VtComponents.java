package falseresync.vivatech.common.data;

import falseresync.lib.registry.RegistryObject;
import net.minecraft.component.ComponentType;
import net.minecraft.util.math.GlobalPos;

public class VtComponents {
    public static final @RegistryObject ComponentType<GlobalPos> CONNECTION =
            ComponentType.<GlobalPos>builder().codec(GlobalPos.CODEC).packetCodec(GlobalPos.PACKET_CODEC).build();
}

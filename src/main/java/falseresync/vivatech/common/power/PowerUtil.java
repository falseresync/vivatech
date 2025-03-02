package falseresync.vivatech.common.power;

import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import net.minecraft.util.math.ChunkPos;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;

public class PowerUtil {
    public static Map<ChunkPos, Set<Wire>> createWireMap() {
        return new Object2ObjectRBTreeMap<>(Comparator.comparingLong(ChunkPos::toLong));
    }
}

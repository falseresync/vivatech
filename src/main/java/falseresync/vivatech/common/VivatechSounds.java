package falseresync.vivatech.common;

import static falseresync.vivatech.common.Vivatech.vtId;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public class VivatechSounds {
    public static final SoundEvent STAR_PROJECTILE_EXPLODE = r("entity.star_projectile.explode");
    public static final SoundEvent COMET_WARP_ANCHOR_PLACED = r("focus.comet_warp.anchor_placed");
    public static final SoundEvent INSUFFICIENT_CHARGE = r("item.gadget.insufficient_charge");

    private static SoundEvent r(String id) {
        var fullId = vtId(id);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, fullId, SoundEvent.createVariableRangeEvent(fullId));
    }

    public static void init() {
    }
}

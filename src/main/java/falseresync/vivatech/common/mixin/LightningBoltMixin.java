package falseresync.vivatech.common.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import falseresync.vivatech.common.data.VivatechAttachments;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightningBolt.class)
public abstract class LightningBoltMixin {
    @WrapWithCondition(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V",
                    ordinal = 0))
    private boolean vivatech$tick$removeThunder(Level instance, double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch, boolean useDistance) {
        return !((LightningBolt) (Object) this).hasAttached(VivatechAttachments.THUNDERLESS_LIGHTNING);
    }

    @WrapOperation(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;playLocalSound(DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFZ)V",
                    ordinal = 1))
    private void vivatech$tick$changeSoundCategory(Level instance, double x, double y, double z, SoundEvent sound, SoundSource category, float volume, float pitch, boolean useDistance, Operation<Void> original) {
        if (((LightningBolt) (Object) this).hasAttached(VivatechAttachments.THUNDERLESS_LIGHTNING)) {
            original.call(instance, x, y, z, sound, SoundSource.PLAYERS, 1.0f, pitch, true);
        } else {
            original.call(instance, x, y, z, sound, category, volume, pitch, useDistance);
        }
    }
}

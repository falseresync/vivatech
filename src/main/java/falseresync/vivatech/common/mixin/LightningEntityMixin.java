package falseresync.vivatech.common.mixin;

import com.llamalad7.mixinextras.injector.v2.*;
import com.llamalad7.mixinextras.injector.wrapoperation.*;
import falseresync.vivatech.common.item.focus.LightningFocusItem;
import falseresync.vivatech.common.item.focus.*;
import net.minecraft.entity.*;
import net.minecraft.entity.data.*;
import net.minecraft.sound.*;
import net.minecraft.world.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(LightningEntity.class)
public abstract class LightningEntityMixin implements LightningFocusItem.VivatechLightning {
    @Unique
    private static final TrackedData<Boolean> THUNDERLESS = DataTracker.registerData(LightningEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    @WrapWithCondition(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V",
                    ordinal = 0))
    private boolean vivatech$tick$removeThunder(World instance, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance) {
        return !((LightningEntity) (Object) this).getDataTracker().get(THUNDERLESS);
    }

    @WrapOperation(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;playSound(DDDLnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FFZ)V",
                    ordinal = 1))
    private void vivatech$tick$changeSoundCategory(World instance, double x, double y, double z, SoundEvent sound, SoundCategory category, float volume, float pitch, boolean useDistance, Operation<Void> original) {
        if (((LightningEntity) (Object) this).getDataTracker().get(THUNDERLESS)) {
            original.call(instance, x, y, z, sound, SoundCategory.PLAYERS, 1.0f, pitch, true);
        } else {
            original.call(instance, x, y, z, sound, category, volume, pitch, useDistance);
        }
    }


    @Inject(method = "initDataTracker", at = @At("TAIL"))
    private void vivatech$initDataTracker(DataTracker.Builder builder, CallbackInfo ci) {
        builder.add(THUNDERLESS, false);
    }

    @Override
    public void vivatech$setThunderless() {
        ((LightningEntity) (Object) this).getDataTracker().set(THUNDERLESS, true, true);
    }
}

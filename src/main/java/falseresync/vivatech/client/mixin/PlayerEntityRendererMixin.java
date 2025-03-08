package falseresync.vivatech.client.mixin;

import falseresync.wizcraft.client.render.entity.*;
import net.minecraft.client.network.*;
import net.minecraft.client.render.entity.*;
import net.minecraft.client.render.entity.model.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(value = PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> implements EnergyVeilFeatureRenderer.Accessor {
    @Mutable
    @Unique
    public EnergyVeilFeatureRenderer<AbstractClientPlayerEntity> energyVeilFeatureRenderer;

    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void wizcraft$init(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
        energyVeilFeatureRenderer = new EnergyVeilFeatureRenderer<>(this, ctx.getModelLoader());
        addFeature(energyVeilFeatureRenderer);
    }

    @Override
    @Unique
    public EnergyVeilFeatureRenderer<AbstractClientPlayerEntity> wizcraft$getEnergyVeilRenderer() {
        return energyVeilFeatureRenderer;
    }
}

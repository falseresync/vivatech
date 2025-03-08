package falseresync.vivatech.client.mixin;

import falseresync.vivatech.client.rendering.entity.EnergyVeilFeatureRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin extends LivingEntityRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> implements EnergyVeilFeatureRenderer.Accessor {
    @Mutable
    @Unique
    public EnergyVeilFeatureRenderer<AbstractClientPlayerEntity> energyVeilFeatureRenderer;

    public PlayerEntityRendererMixin(EntityRendererFactory.Context ctx, PlayerEntityModel<AbstractClientPlayerEntity> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    // Cannot use the Fabric API for this because I need to have the renderer reference stored for convenience
    @Inject(method = "<init>", at = @At("TAIL"))
    public void vivatech$init(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci) {
        energyVeilFeatureRenderer = new EnergyVeilFeatureRenderer<>(this, ctx.getModelLoader());
        addFeature(energyVeilFeatureRenderer);
    }

    @Override
    @Unique
    public EnergyVeilFeatureRenderer<AbstractClientPlayerEntity> vivatech$getEnergyVeilRenderer() {
        return energyVeilFeatureRenderer;
    }
}
package falseresync.vivatech.client.mixin;

import falseresync.vivatech.client.rendering.entity.EnergyVeilFeatureRenderer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> implements EnergyVeilFeatureRenderer.Accessor {
    @Mutable
    @Unique
    public EnergyVeilFeatureRenderer<AbstractClientPlayer> energyVeilFeatureRenderer;

    public PlayerRendererMixin(EntityRendererProvider.Context ctx, PlayerModel<AbstractClientPlayer> model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    // Cannot use the Fabric API for this because I need to have the renderer reference stored for convenience
    @Inject(method = "<init>", at = @At("TAIL"))
    public void vivatech$init(EntityRendererProvider.Context ctx, boolean slim, CallbackInfo ci) {
        energyVeilFeatureRenderer = new EnergyVeilFeatureRenderer<>(this, ctx.getModelSet());
        addLayer(energyVeilFeatureRenderer);
    }

    @Override
    @Unique
    public EnergyVeilFeatureRenderer<AbstractClientPlayer> vivatech$getEnergyVeilRenderer() {
        return energyVeilFeatureRenderer;
    }
}
package falseresync.vivatech.client;

import falseresync.vivatech.client.model.entity.AutomatonEntityModel;
import falseresync.vivatech.client.render.entity.AutomatonEntityRenderer;
import falseresync.vivatech.common.entity.VivatechEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class VivatechClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        registerEntityRenderers();
        registerEntityModelData();
    }

    public void registerEntityRenderers() {
        EntityRendererRegistry.register(VivatechEntities.AUTOMATON, AutomatonEntityRenderer::new);
    }

    public void registerEntityModelData() {
        EntityModelLayerRegistry.registerModelLayer(AutomatonEntityModel.LAYER_MAIN, AutomatonEntityModel::getTexturedModelData);
    }
}

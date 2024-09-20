package falseresync.vivatech.client.render.entity;

import falseresync.vivatech.client.model.entity.AutomatonEntityModel;
import falseresync.vivatech.common.entity.AutomatonEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

import static falseresync.vivatech.common.Vivatech.vtTexId;

public class AutomatonEntityRenderer extends MobEntityRenderer<AutomatonEntity, AutomatonEntityModel> {
    public static final Identifier TEXTURE = vtTexId("entity/automaton");

    public AutomatonEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new AutomatonEntityModel(context.getPart(AutomatonEntityModel.LAYER_MAIN)), 0.5f);
    }

    @Override
    public Identifier getTexture(AutomatonEntity entity) {
        return TEXTURE;
    }
}

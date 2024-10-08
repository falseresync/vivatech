package falseresync.vivatech.client.model.entity;

import falseresync.vivatech.common.entity.AutomatonEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.util.math.MatrixStack;

import static falseresync.vivatech.common.VivatechCommon.vtId;

public class AutomatonEntityModel extends EntityModel<AutomatonEntity> {
    public static final EntityModelLayer LAYER_MAIN = new EntityModelLayer(vtId("automaton"), "main");

    private final ModelPart partCube;

    public AutomatonEntityModel(ModelPart root) {
        partCube = root.getChild("cube");
    }

    @Override
    public void setAngles(AutomatonEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        partCube.render(matrices, vertices, light, overlay, color);
    }

    public static TexturedModelData getTexturedModelData() {
        var modelData = new ModelData();
        var root = modelData.getRoot();
        root.addChild(
                EntityModelPartNames.CUBE,
                ModelPartBuilder.create().uv(0, 0).cuboid(-6F, 12F, -6F, 12F, 12F, 12F),
                ModelTransform.pivot(0F, 0F, 0F));
        return TexturedModelData.of(modelData, 64, 64);
    }
}

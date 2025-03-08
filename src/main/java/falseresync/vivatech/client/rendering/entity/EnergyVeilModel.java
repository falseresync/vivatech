package falseresync.vivatech.client.rendering.entity;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.config.VivatechConfig;
import falseresync.vivatech.common.entity.EnergyVeilEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.entity.player.PlayerEntity;

@SuppressWarnings("FieldCanBeLocal")
public class EnergyVeilModel extends SinglePartEntityModel<PlayerEntity> {
    public static final String SCREEN_LEFT_BOTTOM = "screen_left_bottom";
    public static final String SCREEN_RIGHT_BOTTOM = "screen_right_bottom";
    public static final String SCREEN_LEFT_MIDDLE = "screen_left_middle";
    public static final String SCREEN_RIGHT_MIDDLE = "screen_right_middle";
    private final ModelPart root;
    private final ModelPart screenLeftBottom;
    private final ModelPart screenLeftMiddle;
    private final ModelPart screenRightBottom;
    private final ModelPart screenRightMiddle;

    public EnergyVeilModel(ModelPart root) {
        this.root = root;
        screenLeftBottom = root.getChild(SCREEN_LEFT_BOTTOM);
        screenLeftMiddle = root.getChild(SCREEN_LEFT_MIDDLE);
        screenRightBottom = root.getChild(SCREEN_RIGHT_BOTTOM);
        screenRightMiddle = root.getChild(SCREEN_RIGHT_MIDDLE);
    }

    public static TexturedModelData getTexturedModelData() {
        var modelData = new ModelData();
        var modelPartData = modelData.getRoot();
        var cuboidBuilder = ModelPartBuilder.create()
                .uv(0, 0)
                .cuboid(0f, -8f, -8f, 0.1f, 16f, 16f, new Dilation(0, -2.5f, -2.5f));
        modelPartData.addChild(SCREEN_LEFT_BOTTOM, cuboidBuilder, ModelTransform.pivot(0, -4, -6f));
        modelPartData.addChild(SCREEN_LEFT_MIDDLE, cuboidBuilder, ModelTransform.pivot(0, 12, -6f));
        modelPartData.addChild(SCREEN_RIGHT_BOTTOM, cuboidBuilder, ModelTransform.pivot(0, 16, 6f));
        modelPartData.addChild(SCREEN_RIGHT_MIDDLE, cuboidBuilder, ModelTransform.pivot(0, 32, 6f));
        return TexturedModelData.of(modelData, 16, 16);
    }

    @Override
    public ModelPart getPart() {
        return root;
    }

    @Override
    public void setAngles(PlayerEntity entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
    }

    @Override
    public void animateModel(PlayerEntity entity, float limbAngle, float limbDistance, float tickDelta) {
        throw new UnsupportedOperationException();
    }

    public void animateModel(EnergyVeilEntity veil, float limbAngle, float limbDistance, float tickDelta) {
        if (Vivatech.getConfig().animationQuality == VivatechConfig.AnimationQuality.DEFAULT) {
            getPart().traverse().forEach(ModelPart::resetTransform);
            updateAnimation(veil.slideAnimationState, EnergyVeilAnimations.SLIDE.get(), veil.age + tickDelta);
        }
    }
}

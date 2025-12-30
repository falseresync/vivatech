package falseresync.vivatech.client.rendering.entity;

import falseresync.vivatech.common.Vivatech;
import falseresync.vivatech.common.config.VivatechConfig;
import falseresync.vivatech.common.entity.EnergyVeilEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.world.entity.player.Player;

@SuppressWarnings("FieldCanBeLocal")
public class EnergyVeilModel extends HierarchicalModel<Player> {
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

    public static LayerDefinition getTexturedModelData() {
        var modelData = new MeshDefinition();
        var modelPartData = modelData.getRoot();
        var cuboidBuilder = CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(0f, -8f, -8f, 0.1f, 16f, 16f, new CubeDeformation(0, -2.5f, -2.5f));
        modelPartData.addOrReplaceChild(SCREEN_LEFT_BOTTOM, cuboidBuilder, PartPose.offset(0, -4, -6f));
        modelPartData.addOrReplaceChild(SCREEN_LEFT_MIDDLE, cuboidBuilder, PartPose.offset(0, 12, -6f));
        modelPartData.addOrReplaceChild(SCREEN_RIGHT_BOTTOM, cuboidBuilder, PartPose.offset(0, 16, 6f));
        modelPartData.addOrReplaceChild(SCREEN_RIGHT_MIDDLE, cuboidBuilder, PartPose.offset(0, 32, 6f));
        return LayerDefinition.create(modelData, 16, 16);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(Player entity, float f, float g, float h, float i, float j) {
    }

    @Override
    public void prepareMobModel(Player entity, float limbAngle, float limbDistance, float tickDelta) {
        throw new UnsupportedOperationException();
    }

    public void prepareMobModel(EnergyVeilEntity veil, float limbAngle, float limbDistance, float tickDelta) {
        if (Vivatech.getConfig().animationQuality == VivatechConfig.AnimationQuality.DEFAULT) {
            root().getAllParts().forEach(ModelPart::resetPose);
            animate(veil.slideAnimationState, EnergyVeilAnimations.SLIDE.get(), veil.tickCount + tickDelta);
        }
    }
}

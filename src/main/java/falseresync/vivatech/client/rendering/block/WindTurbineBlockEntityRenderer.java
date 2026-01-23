package falseresync.vivatech.client.rendering.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import falseresync.vivatech.Vivatech;
import falseresync.vivatech.world.block.WindTurbineBlock;
import falseresync.vivatech.world.blockentity.WindTurbineBlockEntity;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.MaterialSet;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class WindTurbineBlockEntityRenderer implements BlockEntityRenderer<WindTurbineBlockEntity, WindTurbineBlockEntityRenderer.State> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(Vivatech.id("wind_turbine"), "main");
    public static final Material TEX = new Material(TextureAtlas.LOCATION_BLOCKS, Vivatech.id("block/wind_turbine"));
    private final MaterialSet materials;
    private final ModelPart model;

    public WindTurbineBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        materials = ctx.materials();
        model = ctx.bakeLayer(LAYER);
        model.setInitialPose(PartPose.offset(8, 8, 0));
        model.resetPose();
    }

    public static LayerDefinition getTexturedModelData() {
        var modelData = new MeshDefinition();
        var root = modelData.getRoot();
        var blade = CubeListBuilder.create().texOffs(0, 0).addBox(-12, 0, 0, 12.0F, 60.0F, 2.0F);
        root.addOrReplaceChild("blade1", blade, PartPose.offsetAndRotation(0, 0, 0, 0, Mth.HALF_PI, 0));
        root.addOrReplaceChild("blade2", blade, PartPose.offsetAndRotation(0, 0, 0, 0, Mth.HALF_PI, 2 * Mth.PI / 3));
        root.addOrReplaceChild("blade3", blade, PartPose.offsetAndRotation(0, 0, 0, 0, Mth.HALF_PI, 2 * 2 * Mth.PI / 3));
        return LayerDefinition.create(modelData, 32, 64);
    }

    @Override
    public State createRenderState() {
        return new State();
    }

    @Override
    public void extractRenderState(WindTurbineBlockEntity blockEntity, State state, float partialTicks, Vec3 cameraPosition, ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderState.extractBase(blockEntity, state, breakProgress);
        state.rotationProgress = blockEntity.getRotationProgress(partialTicks);
    }

    @Override
    public void submit(State state, PoseStack matrices, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        matrices.pushPose();
        matrices.rotateAround(Axis.YN.rotationDegrees(state.blockState.getValue(WindTurbineBlock.FACING).getOpposite().toYRot()), 0.5f, 0.5f, 0.5f);
        model.zRot = state.rotationProgress;
        submitNodeCollector.submitModelPart(model, matrices, TEX.renderType(RenderTypes::entityCutout), state.lightCoords, OverlayTexture.NO_OVERLAY, materials.get(TEX));
        matrices.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    public static class State extends BlockEntityRenderState {
        public float rotationProgress;
    }
}

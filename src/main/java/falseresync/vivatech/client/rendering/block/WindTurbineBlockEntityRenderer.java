package falseresync.vivatech.client.rendering.block;

import falseresync.vivatech.common.block.WindTurbineBlock;
import falseresync.vivatech.common.blockentity.WindTurbineBlockEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.util.Mth;

import static falseresync.vivatech.common.Vivatech.vtId;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;

public class WindTurbineBlockEntityRenderer implements BlockEntityRenderer<WindTurbineBlockEntity> {
    public static final ModelLayerLocation LAYER = new ModelLayerLocation(vtId("wind_turbine"), "main");
    public static final Material TEX = new Material(TextureAtlas.LOCATION_BLOCKS, vtId("block/wind_turbine"));
    private final ModelPart model;

    public WindTurbineBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
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
    public void render(WindTurbineBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        matrices.pushPose();
        matrices.rotateAround(Axis.YN.rotationDegrees(entity.getBlockState().getValue(WindTurbineBlock.FACING).getOpposite().toYRot()), 0.5f, 0.5f, 0.5f);
        model.zRot = entity.getRotationProgress(tickDelta);
        model.render(matrices, TEX.buffer(vertexConsumers, RenderType::entityCutout), light, overlay);
        matrices.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(WindTurbineBlockEntity blockEntity) {
        return true;
    }
}

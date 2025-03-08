package falseresync.vivatech.client.rendering.entity;

import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;
import org.joml.Vector3f;

import java.util.function.Supplier;

import static falseresync.vivatech.client.rendering.entity.EnergyVeilModel.*;

public class EnergyVeilAnimations {
    public static final Transformation SCREEN_TRANSLATION_UP = new Transformation(
            Transformation.Targets.TRANSLATE,
            new Keyframe(0.000F, new Vector3f(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(1.000F, new Vector3f(0.0F, 16.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(1.000F, new Vector3f(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(2.000F, new Vector3f(0.0F, 16.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(2.000F, new Vector3f(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
    );
    public static final Transformation SCREEN_TRANSLATION_DOWN = new Transformation(
            Transformation.Targets.TRANSLATE,
            new Keyframe(0.000F, new Vector3f(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(1.000F, new Vector3f(0.0F, -16.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(1.000F, new Vector3f(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(2.000F, new Vector3f(0.0F, -16.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(2.000F, new Vector3f(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
    );
    public static final Transformation SCREEN_SCALING_IN = new Transformation(
            Transformation.Targets.SCALE,
            new Keyframe(0.000F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(0.250F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(1.000F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(1.000F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(1.250F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(2.000F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR)
    );
    public static final Transformation SCREEN_SCALING_OUT = new Transformation(
            Transformation.Targets.SCALE,
            new Keyframe(0.000F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(0.750F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(1.000F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(1.000F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(1.750F, AnimationHelper.createScalingVector(1.0F, 1.0F, 1.0F), Transformation.Interpolations.LINEAR),
            new Keyframe(2.000F, AnimationHelper.createScalingVector(0.0F, 0.0F, 0.0F), Transformation.Interpolations.LINEAR)
    );
    public static final Supplier<Animation> SLIDE = () -> Animation.Builder.create(2).looping()
            .addBoneAnimation(SCREEN_LEFT_BOTTOM, SCREEN_TRANSLATION_UP)
            .addBoneAnimation(SCREEN_LEFT_MIDDLE, SCREEN_TRANSLATION_UP)
            .addBoneAnimation(SCREEN_RIGHT_BOTTOM, SCREEN_TRANSLATION_DOWN)
            .addBoneAnimation(SCREEN_RIGHT_MIDDLE, SCREEN_TRANSLATION_DOWN)
            .addBoneAnimation(SCREEN_LEFT_BOTTOM, SCREEN_SCALING_IN)
            .addBoneAnimation(SCREEN_LEFT_MIDDLE, SCREEN_SCALING_OUT)
            .addBoneAnimation(SCREEN_RIGHT_BOTTOM, SCREEN_SCALING_OUT)
            .addBoneAnimation(SCREEN_RIGHT_MIDDLE, SCREEN_SCALING_IN)
            .build();
}

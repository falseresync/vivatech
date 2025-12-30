package falseresync.vivatech.client.rendering.entity;

import org.joml.Vector3f;

import java.util.function.Supplier;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

import static falseresync.vivatech.client.rendering.entity.EnergyVeilModel.*;

public class EnergyVeilAnimations {
    public static final AnimationChannel SCREEN_TRANSLATION_UP = new AnimationChannel(
            AnimationChannel.Targets.POSITION,
            new Keyframe(0.000F, new Vector3f(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.000F, new Vector3f(0.0F, 16.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.000F, new Vector3f(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.000F, new Vector3f(0.0F, 16.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.000F, new Vector3f(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
    );
    public static final AnimationChannel SCREEN_TRANSLATION_DOWN = new AnimationChannel(
            AnimationChannel.Targets.POSITION,
            new Keyframe(0.000F, new Vector3f(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.000F, new Vector3f(0.0F, -16.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.000F, new Vector3f(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.000F, new Vector3f(0.0F, -16.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.000F, new Vector3f(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
    );
    public static final AnimationChannel SCREEN_SCALING_IN = new AnimationChannel(
            AnimationChannel.Targets.SCALE,
            new Keyframe(0.000F, KeyframeAnimations.scaleVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.250F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.000F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.000F, KeyframeAnimations.scaleVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.250F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.000F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR)
    );
    public static final AnimationChannel SCREEN_SCALING_OUT = new AnimationChannel(
            AnimationChannel.Targets.SCALE,
            new Keyframe(0.000F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(0.750F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.000F, KeyframeAnimations.scaleVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.000F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(1.750F, KeyframeAnimations.scaleVec(1.0F, 1.0F, 1.0F), AnimationChannel.Interpolations.LINEAR),
            new Keyframe(2.000F, KeyframeAnimations.scaleVec(0.0F, 0.0F, 0.0F), AnimationChannel.Interpolations.LINEAR)
    );
    public static final Supplier<AnimationDefinition> SLIDE = () -> AnimationDefinition.Builder.withLength(2).looping()
            .addAnimation(SCREEN_LEFT_BOTTOM, SCREEN_TRANSLATION_UP)
            .addAnimation(SCREEN_LEFT_MIDDLE, SCREEN_TRANSLATION_UP)
            .addAnimation(SCREEN_RIGHT_BOTTOM, SCREEN_TRANSLATION_DOWN)
            .addAnimation(SCREEN_RIGHT_MIDDLE, SCREEN_TRANSLATION_DOWN)
            .addAnimation(SCREEN_LEFT_BOTTOM, SCREEN_SCALING_IN)
            .addAnimation(SCREEN_LEFT_MIDDLE, SCREEN_SCALING_OUT)
            .addAnimation(SCREEN_RIGHT_BOTTOM, SCREEN_SCALING_OUT)
            .addAnimation(SCREEN_RIGHT_MIDDLE, SCREEN_SCALING_IN)
            .build();
}

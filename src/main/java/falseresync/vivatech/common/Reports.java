package falseresync.vivatech.common;

import falseresync.vivatech.common.VivatechSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class Reports {
    public static void insufficientCharge(Player player) {
        player.playNotifySound(VivatechSounds.INSUFFICIENT_CHARGE, SoundSource.PLAYERS, 1f, 1f);
        player.displayClientMessage(Component.translatable("hud.vivatech.gadget.insufficient_charge").withStyle(ChatFormatting.DARK_RED), true);
    }

    private static void addSparkles(Level world, Vec3 pos) {
        addParticle(world, ParticleTypes.FIREWORK, pos, 5, 10);
    }

    private static void addSmoke(Level world, Vec3 pos) {
        addParticle(world, ParticleTypes.WHITE_SMOKE, pos, 5, 10);
    }

    private static void addParticle(Level world, ParticleOptions parameters, Vec3 pos, int minAmount, int maxAmount) {
        var random = world.getRandom();
        if (world instanceof ServerLevel serverWorld) {
            serverWorld.sendParticles(
                    parameters, pos.x, pos.y, pos.z,
                    random.nextIntBetweenInclusive(minAmount, maxAmount),
                    (random.nextFloat() - 0.5) / 2,
                    random.nextFloat() / 2,
                    (random.nextFloat() - 0.5) / 2,
                    0.15);
        } else {
            for (int i = 0; i < random.nextIntBetweenInclusive(minAmount, maxAmount); i++) {
                world.addParticle(
                        parameters, pos.x, pos.y, pos.z,
                        (random.nextFloat() - 0.5) / 2,
                        random.nextFloat() / 2,
                        (random.nextFloat() - 0.5) / 2);
            }
        }
    }
}

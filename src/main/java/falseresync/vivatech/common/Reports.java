package falseresync.vivatech.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Reports {
    public static void insufficientCharge(PlayerEntity player) {
        player.playSoundToPlayer(VivatechSounds.INSUFFICIENT_CHARGE, SoundCategory.PLAYERS, 1f, 1f);
        player.sendMessage(Text.translatable("hud.vivatech.gadget.insufficient_charge").formatted(Formatting.DARK_RED), true);
    }

    private static void addSparkles(World world, Vec3d pos) {
        addParticle(world, ParticleTypes.FIREWORK, pos, 5, 10);
    }

    private static void addSmoke(World world, Vec3d pos) {
        addParticle(world, ParticleTypes.WHITE_SMOKE, pos, 5, 10);
    }

    private static void addParticle(World world, ParticleEffect parameters, Vec3d pos, int minAmount, int maxAmount) {
        var random = world.getRandom();
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(
                    parameters, pos.x, pos.y, pos.z,
                    random.nextBetween(minAmount, maxAmount),
                    (random.nextFloat() - 0.5) / 2,
                    random.nextFloat() / 2,
                    (random.nextFloat() - 0.5) / 2,
                    0.15);
        } else {
            for (int i = 0; i < random.nextBetween(minAmount, maxAmount); i++) {
                world.addParticle(
                        parameters, pos.x, pos.y, pos.z,
                        (random.nextFloat() - 0.5) / 2,
                        random.nextFloat() / 2,
                        (random.nextFloat() - 0.5) / 2);
            }
        }
    }
}

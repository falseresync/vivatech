package falseresync.vivatech.client.particle;

import net.minecraft.client.particle.GlowParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;

public class ChargingParticleFactory implements ParticleFactory<SimpleParticleType> {
    private final SpriteProvider spriteProvider;

    public ChargingParticleFactory(SpriteProvider spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(SimpleParticleType simpleParticleType, ClientWorld clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        var glowParticle = new GlowParticle(clientWorld, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
        if (clientWorld.random.nextBoolean()) {
            glowParticle.setColor(0.6F, 1.0F, 0.8F);
        } else {
            glowParticle.setColor(0.08F, 0.4F, 0.4F);
        }

        glowParticle.setMaxAge((int) (16.0 / (clientWorld.random.nextDouble() * 0.8 + 0.2)));
        return glowParticle;
    }
}

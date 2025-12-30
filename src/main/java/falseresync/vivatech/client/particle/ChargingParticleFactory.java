package falseresync.vivatech.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.GlowParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;

public class ChargingParticleFactory implements ParticleProvider<SimpleParticleType> {
    private final SpriteSet spriteProvider;

    public ChargingParticleFactory(SpriteSet spriteProvider) {
        this.spriteProvider = spriteProvider;
    }

    @Override
    public Particle createParticle(SimpleParticleType simpleParticleType, ClientLevel clientWorld, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        var glowParticle = new GlowParticle(clientWorld, x, y, z, velocityX, velocityY, velocityZ, this.spriteProvider);
        if (clientWorld.random.nextBoolean()) {
            glowParticle.setColor(0.6F, 1.0F, 0.8F);
        } else {
            glowParticle.setColor(0.08F, 0.4F, 0.4F);
        }

        glowParticle.setLifetime((int) (16.0 / (clientWorld.random.nextDouble() * 0.8 + 0.2)));
        return glowParticle;
    }
}

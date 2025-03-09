package falseresync.vivatech.client.particle;

import falseresync.vivatech.common.VivatechParticleTypes;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;

public class VivatechParticleFactories {
    public static void init() {
        ParticleFactoryRegistry.getInstance().register(VivatechParticleTypes.CHARGING, ChargingParticleFactory::new);
    }
}

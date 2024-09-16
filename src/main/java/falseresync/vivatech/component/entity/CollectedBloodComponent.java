package falseresync.vivatech.component.entity;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public class CollectedBloodComponent implements AutoSyncedComponent, ServerTickingComponent {
    private int amount = 0;
    private final PlayerEntity player;

    public CollectedBloodComponent(PlayerEntity player) {
        this.player = player;
    }

    public int getAmount() {
        return amount;
    }

    public void add(int amount) {
        this.amount += amount;
    }

    @Override
    public void serverTick() {
        var decaySpeed = 1f;
        decaySpeed *= player.hasStatusEffect(StatusEffects.WEAKNESS)    ? 10.0f : 1.0f;
        decaySpeed *= player.hasStatusEffect(StatusEffects.WITHER)      ? 10.0f : 1.0f;
        decaySpeed *= player.hasStatusEffect(StatusEffects.HASTE)       ? 5.0f : 1.0f;
        decaySpeed *= player.hasStatusEffect(StatusEffects.NAUSEA)      ? 5.0f : 1.0f;
        decaySpeed *= player.hasStatusEffect(StatusEffects.BAD_OMEN)    ? 2.0f : 1.0f;
        decaySpeed *= player.hasStatusEffect(StatusEffects.UNLUCK)      ? 2.0f : 1.0f;
        if (player.getRandom().nextFloat() < decaySpeed / (60 /*s*/ * 20 /*tps*/) /* roughly once per minute */) {
            amount -= 1;
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        amount = 0;
        if (tag.contains("amount", NbtElement.INT_TYPE)) {
            amount = tag.getInt("amount");
        }
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        tag.putInt("amount", amount);
    }
}

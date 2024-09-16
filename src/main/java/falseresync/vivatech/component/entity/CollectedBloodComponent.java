package falseresync.vivatech.component.entity;

import com.google.common.base.Preconditions;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

public class CollectedBloodComponent implements AutoSyncedComponent, ServerTickingComponent {
    private final PlayerEntity player;
    private int amount = 0;

    public CollectedBloodComponent(PlayerEntity player) {
        this.player = player;
    }

    public int getAmount() {
        return amount;
    }

    public void add(int amount) {
        Preconditions.checkArgument(amount > 0, "amount must be greater than 0");
        this.amount += amount;
    }

    @Override
    public void serverTick() {
        if (amount <= 0) return;
        var decaySpeed = 1f;
        decaySpeed *= player.hasStatusEffect(StatusEffects.WEAKNESS) ? 10.0f : 1.0f;
        decaySpeed *= player.hasStatusEffect(StatusEffects.WITHER) ? 10.0f : 1.0f;
        decaySpeed *= player.hasStatusEffect(StatusEffects.HASTE) ? 5.0f : 1.0f;
        decaySpeed *= player.hasStatusEffect(StatusEffects.NAUSEA) ? 5.0f : 1.0f;
        decaySpeed *= player.hasStatusEffect(StatusEffects.BAD_OMEN) ? 2.0f : 1.0f;
        decaySpeed *= player.hasStatusEffect(StatusEffects.UNLUCK) ? 2.0f : 1.0f;
        if (player.getRandom().nextFloat() < decaySpeed / (60 /*s*/ * 20 /*tps*/) /* roughly once per minute */) {
            amount -= 1;
        }
    }

    @Override
    public void readFromNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        amount = 0;
        if (tag.contains("amount", NbtElement.INT_TYPE)) amount = tag.getInt("amount");
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (amount <= 0) amount = 0;
        tag.putInt("amount", amount);
    }
}

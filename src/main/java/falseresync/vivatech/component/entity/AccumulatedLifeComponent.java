package falseresync.vivatech.component.entity;

import com.google.common.base.Preconditions;
import falseresync.vivatech.item.LifeAccumulatingItem;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class AccumulatedLifeComponent implements AutoSyncedComponent, ServerTickingComponent {
    private final PlayerEntity player;
    private final int maxAmount = 1000;
    private int amount = 0;

    public AccumulatedLifeComponent(PlayerEntity player) {
        this.player = player;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public int getAmount() {
        return amount;
    }

    public void increment(int by) {
        Preconditions.checkArgument(by > 0, "amount must be greater than 0");
        amount = Math.min(amount + by, maxAmount);
    }

    public void decrement(int by) {
        Preconditions.checkArgument(by > 0, "amount must be greater than 0");
        amount = Math.max(amount - by, 0);
    }

    @Override
    public void serverTick() {
        if (amount <= 0) return;
        offloadLife();

        if (amount <= 0) return;
        decayLife();
    }

    private void offloadLife() {
        var speed = Math.min((int) calculateOffloadSpeedPerMinute(), amount);
        if (speed == 0) return;
        System.out.println("Speed! " + speed);

        List<ItemStack> stacks = player.getInventory().main.stream()
                .filter(stack -> stack.getItem() instanceof LifeAccumulatingItem lifeAccumulatingItem
                        && !lifeAccumulatingItem.isFull(stack))
                .collect(Collectors.toCollection(ArrayList::new));
        if (stacks.isEmpty()) return;
        System.out.println("Stacks! " + stacks.size());

        int transferablePerAccumulatingItem = speed / stacks.size();
        if (transferablePerAccumulatingItem == 0) {
            int remainder = speed % stacks.size();
            Collections.shuffle(stacks);
            stacks = stacks.subList(0, remainder - 1);
            transferablePerAccumulatingItem = remainder;
        }
        System.out.println("per! " + transferablePerAccumulatingItem);

        var transferred = 0;
        for (var stack : stacks) {
            var accumulatingItem = (LifeAccumulatingItem) stack.getItem();
            transferred += accumulatingItem.incrementLife(stack, transferablePerAccumulatingItem, false);
        }
        amount -= transferred;
    }

    private float calculateOffloadSpeedPerMinute() {
        return calculateOffloadChanceBase() * calculateOffloadChanceMultiplier() / (60 /*s*/ * 20 /*tps*/);
    }

    private float calculateOffloadChanceBase() {
        return 100;
    }

    private float calculateOffloadChanceMultiplier() {
        return 1;
    }

    private void decayLife() {
        if (player.getRandom().nextFloat() < calculateDecayChance()) {
            amount -= 1;
        }
    }

    private float calculateDecayChance() {
        return calculateDecayChanceBase() * calculateDecayChanceMultiplier() / (60 /*s*/ * 20 /*tps*/);
    }

    private float calculateDecayChanceBase() {
        return 1;
    }

    private float calculateDecayChanceMultiplier() {
        var multiplier = 1f;
        multiplier *= player.hasStatusEffect(StatusEffects.WITHER) ? 10f : 1;
        multiplier *= player.hasStatusEffect(StatusEffects.WEAKNESS) ? 10f : 1;
        multiplier *= player.hasStatusEffect(StatusEffects.HASTE) ? 7.5f : 1;
        multiplier *= player.hasStatusEffect(StatusEffects.NAUSEA) ? 7.5f : 1;
        multiplier *= player.hasStatusEffect(StatusEffects.BAD_OMEN) ? 5f : 1;
        multiplier *= player.hasStatusEffect(StatusEffects.POISON) ? 5f : 1;
        multiplier *= player.hasStatusEffect(StatusEffects.TRIAL_OMEN) ? 2.5f : 1;
        multiplier *= player.hasStatusEffect(StatusEffects.RAID_OMEN) ? 2.5f : 1;
        return multiplier;
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

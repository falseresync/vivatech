package falseresync.vivatech.common.component.entity;

import falseresync.vivatech.api.lifessence.Lifessence;
import falseresync.vivatech.api.lifessence.LifessenceStorage;
import falseresync.vivatech.api.lifessence.base.SimpleLifessenceStorage;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.PlayerInventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryWrapper;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;
import org.ladysnake.cca.api.v3.component.tick.ServerTickingComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerLifessenceStorageComponent extends SimpleLifessenceStorage implements AutoSyncedComponent, ServerTickingComponent {
    private final PlayerEntity player;

    public PlayerLifessenceStorageComponent(PlayerEntity player) {
        super(1000, 0);
        this.player = player;
    }

    @Override
    public boolean supportsInsertion() {
        return true;
    }

    @Override
    public boolean supportsExtraction() {
        return true;
    }

    @Override
    public void serverTick() {
        offload();
        decay();
    }

    private void offload() {
        if (amount <= 0 || !shouldOffload()) return;

        var lifessenceStorages = findLifessenceStorages();
        if (lifessenceStorages.isEmpty()) return;

        var offloadTotal = calculateOffloadTotal();
        if (offloadTotal < lifessenceStorages.size()) {
            Collections.shuffle(lifessenceStorages);
            // since the .size() is an integer and it is bigger than amount
            // it is safe to assume that amount - 1 fits into an integer
            lifessenceStorages = lifessenceStorages.subList(0, (int) (amount - 1));
        }

        // offloadPerStorage * .size() <= offloadTotal is guaranteed by the code above
        var offloadPerStorage = calculateOffloadPerStorage();
        try (var tx = Transaction.openOuter()) {
            var transferred = 0L;
            for (var lifessenceStorage : lifessenceStorages) {
                transferred += lifessenceStorage.insert(offloadPerStorage, tx);
            }
            if (extract(transferred, tx) == transferred) {
                tx.commit();
            }
        }
    }

    private boolean shouldOffload() {
        return player.getRandom().nextFloat() < (20F / (60 /*s*/ * 20 /*tps*/));
    }

    private long calculateOffloadTotal() {
        return 10L;
    }

    private long calculateOffloadPerStorage() {
        return 1L;
    }

    private List<LifessenceStorage> findLifessenceStorages() {
        return PlayerInventoryStorage.of(player).getSlots().stream()
                .flatMap(slot -> Stream.ofNullable(ContainerItemContext.ofPlayerSlot(player, slot).find(Lifessence.ITEM)))
                .filter(LifessenceStorage::supportsInsertion)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void decay() {
        if (amount > 0 && shouldDecay()) {
            amount -= 1;
        }
    }

    private boolean shouldDecay() {
        return player.getRandom().nextFloat() <
                (calculateDecayChanceBase() * calculateDecayChanceMultiplier() / (60 /*s*/ * 20 /*tps*/));
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
        if (tag.contains("amount", NbtElement.LONG_TYPE)) amount = tag.getLong("amount");
    }

    @Override
    public void writeToNbt(NbtCompound tag, RegistryWrapper.WrapperLookup registryLookup) {
        if (amount <= 0) amount = 0;
        tag.putLong("amount", amount);
    }
}
